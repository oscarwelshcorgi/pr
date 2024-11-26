package com.pr.util.pasing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.Date;

@Component
@EnableScheduling
public class JjangPaser {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    //@Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Scheduled(cron = "0 30 11 * * ?") // 리눅스 서버랑 6시간 차이남. 리눅스가 6시간 느림.
    public void parseWebsiteAndStoreInDatabase() {
        Timestamp currentDate = new Timestamp(new Date().getTime());

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("MySQL 드라이버 로딩 성공");

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                System.out.println("MySQL 연결 성공");

                for (int page = 1; page < 3; page++) {
                    String url = "https://www.jjang0u.com/board/list/fun/" + page;
                    Document doc = Jsoup.connect(url).get();
                    Elements titles = doc.select(".title a");

                    for (Element titleElement : titles) {
                        String title = titleElement.getElementsByAttribute("title").attr("title");
                        String postUrl = "https://www.jjang0u.com" + titleElement.getElementsByAttribute("href").attr("href");

                        String content = parseContent(postUrl);

                        // content에 "oembed" 문자열이 포함되어 있는지 확인
                        if (content.contains("oembed") || content.contains("ytp-muted-autoplay-endscreen-overlay")) {
                            System.out.println("oembed, ytp 포함된 글은 저장하지 않습니다.");
                            continue; // oembed 포함된 글은 저장하지 않고 다음 글로 넘어감
                        }

                        insertIntoDatabase(conn, title, content, currentDate);
                    }
                }

                System.out.println("파싱 종료");
            }
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private String parseContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element contentElement = doc.selectFirst("section#post_content");
        return contentElement != null ? contentElement.html() : "";
    }

    private void insertIntoDatabase(Connection conn, String title, String content, Timestamp date) throws SQLException {
        String sql = "INSERT INTO article (id, title, content, email, delete_yn, create_date, update_date, view_count, board_code) " +
                "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, addWidthToImages(content) + "&nbsp"); // <img> 태그에 width 추가
            pstmt.setString(3, "oscarwelshcorgi@gmail.com");
            pstmt.setString(4, "n");
            pstmt.setTimestamp(5, date);
            pstmt.setTimestamp(6, date);
            pstmt.setString(7, "100");
            pstmt.setString(8, "humor");

            pstmt.executeUpdate();
            System.out.println("INSERT 실행: " + sql);
        }
    }

    private String addWidthToImages(String content) {
        Document doc = Jsoup.parse(content);

        // 이미지 태그에 width 스타일 추가
        Elements imgElements = doc.select("img");
        for (Element img : imgElements) {
            img.attr("style", "width:100%;"); // 이미지 태그에 style 속성 추가
        }

        // 비디오 태그에 width 스타일 추가
        Elements videoElements = doc.select("video");
        for (Element video : videoElements) {
            video.attr("style", "width:100%;"); // 비디오 태그에 style 속성 추가
        }

        return doc.html();
    }
}