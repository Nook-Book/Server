package com.nookbook.domain.user_book.application;

import com.nookbook.domain.challenge.application.ParticipantService;
import com.nookbook.domain.challenge.domain.Participant;
import com.nookbook.domain.timer.application.TimerService;
import com.nookbook.domain.timer.domain.Timer;
import com.nookbook.domain.user.application.UserService;
import com.nookbook.domain.user.domain.User;
import com.nookbook.domain.user.domain.repository.UserRepository;
import com.nookbook.domain.user_book.domain.UserBook;
import com.nookbook.domain.user_book.domain.repository.UserBookRepository;
import com.nookbook.domain.user_book.dto.response.DailyUserBookCalendarRes;
import com.nookbook.domain.user_book.dto.response.MonthlyUserBookCalendarRes;
import com.nookbook.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBookService {
    private final UserBookRepository userBookRepository;

    private final UserService userService;
    private final TimerService timerService;

    private final UserRepository userRepository;
    private final ParticipantService participantService;

    // 사용자의 독서 기록(캘린더) 조회
    public ResponseEntity<?> getUserBookCalendar(UserPrincipal userPrincipal, String date) {
        User user = validateUser(userPrincipal);
        // 날짜 형식 판단 메소드
        // YYYY-MM-DD 형식 / YYYY-MM 형식
        return distinctDateFormat(user, date);

    }

    // 특정 챌린지 참가자의 독서 기록(캘린더) 조회
    public ResponseEntity<?> getUserBookCalendar(UserPrincipal userPrincipal,Long participantId, String date) {
        validateUser(userPrincipal);
        Participant participant = participantService.getParticipant(participantId);
        User participantUser = participant.getUser();
        // 날짜 형식 판단 메소드
        // YYYY-MM-DD 형식 / YYYY-MM 형식
        return distinctDateFormat(participantUser, date);

    }



    private ResponseEntity<?> distinctDateFormat(User user, String date) {
        if (isValidDateFormat(date, "yyyy-MM")) {
            log.info("YYYY-MM 형식");
            List<MonthlyUserBookCalendarRes> monthlyData = getUserBookInfoByMonth(user, date);
            return ResponseEntity.ok(monthlyData);
        } else if (isValidDateFormat(date, "yyyy-MM-dd")) {
            log.info("YYYY-MM-DD 형식");
            DailyUserBookCalendarRes dailyData = getUserBookInfoByDate(user, date);
            return ResponseEntity.ok(dailyData);
        } else {
            log.warn("날짜 형식이 올바르지 않습니다: {}", date);
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM or YYYY-MM-DD.");
        }
    }

    // 날짜 형식 검증 메서드
    private boolean isValidDateFormat(String date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            if (pattern.equals("yyyy-MM")) {
                YearMonth.parse(date, formatter); // YYYY-MM 형식 검증
            } else {
                LocalDate.parse(date, formatter); // YYYY-MM-DD 형식 검증
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    // String -> LocalDateTime 형식으로 변환
    private LocalDateTime convertStringToLocalDateTime(String date) {
        return LocalDateTime.parse(date + "T00:00:00");
    }

    // String -> LocalDate 형식으로 변환
    private LocalDate convertStringToLocalDate(String date) {
        return LocalDate.parse(date);
    }

    private DailyUserBookCalendarRes getUserBookInfoByDate(User user, String date) {
        LocalDate localDate = convertStringToLocalDate(date);
        // 유저의 특정 날짜에 대한 타이머 기록 조회
        List<Timer> timers = timerService.getTimerListByDate(user, localDate);
        log.info("타이머 리스트 수 : " + timers.size());
        String totalReadTime = sumTotalReadTime(timers);
        log.info("총 독서 시간 : " + totalReadTime);

        return DailyUserBookCalendarRes.builder()
                .totalReadTime(totalReadTime)
                .startTime(getStartReadTime(timers))
                .endTime(getEndReadTime(timers))
                .bookList(getBookListByDate(user, date))
                .build();

    }

    private String sumTotalReadTime(List<Timer> timerList) {
        return timerService.sumTotalReadTime(timerList);
    }

    private List<Map<String, String>> getBookListByDate(User user, String date) {
        List<UserBook> userBooks = userBookRepository.findUserBookListByDate(user, convertStringToLocalDate(date));
        List<Map<String, String>> bookList = new ArrayList<>();

        log.info("책 리스트 수 : " + userBooks.size());
        for (UserBook userBook : userBooks) {
            Map<String, String> bookInfo = Map.of(
                    "title", userBook.getBook().getTitle(),
                    "image", userBook.getBook().getImage()
            );
            bookList.add(bookInfo);
        }

        return bookList;
    }

    // 해당 월에 해당하는 날짜들에 대해 getBookListByDate 메소드 호출
    private List<MonthlyUserBookCalendarRes> getUserBookInfoByMonth(User user, String date) {
        LocalDate localDate = LocalDate.parse(date + "-01"); // YYYY-MM 형식을 YYYY-MM-01로 변환
        YearMonth yearMonth = YearMonth.from(localDate);

        List<MonthlyUserBookCalendarRes> monthlyRecords = new ArrayList<>();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            DailyUserBookCalendarRes dailyRecord = getUserBookInfoByDate(user, currentDate.toString());

            MonthlyUserBookCalendarRes monthlyUserBookCalendarRes = MonthlyUserBookCalendarRes.builder()
                    .date(currentDate.toString())
                    .dailyUserBookCalendar(dailyRecord)
                    .build();

            monthlyRecords.add(monthlyUserBookCalendarRes);
        }

        return monthlyRecords;
    }


    private String getStartReadTime (List<Timer> timerList) {
        if(timerList.isEmpty()) {
            return null;
        }
        else{
            // HH:SS 형식으로 변환
            return timerList.get(0).getCreatedAt().toLocalTime().toString();
        }
    }

    // 맨 마지막 타이머의 is_reading이 true인 경우, 기록이 진행중이므로 null 반환
    private String getEndReadTime(List<Timer> timerList) {
        if(timerList.isEmpty() || timerList.get(timerList.size() - 1).isReading()) {
            return null;
        }
        else{
            // HH:SS 형식으로 변환
            return timerList.get(timerList.size() - 1).getCreatedAt().toLocalTime().toString();
        }
    }

    // 사용자 검증 메서드
    private User validateUser(UserPrincipal userPrincipal) {
//        return userService.findByEmail(userPrincipal.getEmail())
//                .orElseThrow(UserNotFoundException::new);
        // userId=1L로 고정
        return userService.findById(1L);
    }
}
