package com.example.blog.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HolidayService {

    // 공공데이터 포털에서 발급받은 Decoding 인증키를 입력하세요.
    private static final String SERVICE_KEY = "0151149634dd20e6ee6de8068059d5ab9f3281c70d522108bce2f207c3312059";
    private static final String API_URL = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo";

    // 연도별 휴일 데이터를 메모리에 캐싱 (API 호출 횟수 절약)
    private final Map<String, List<Map<String, Object>>> holidayCache = new ConcurrentHashMap<>();

    public List<Map<String, Object>> getHolidays(String start, String end) {
        // start 날짜에서 연도 추출 (예: "2025-01-01" -> "2025")
        String year = start.substring(0, 4);
        
        // 캐시에 데이터가 있으면 반환
        if (holidayCache.containsKey(year)) {
            return holidayCache.get(year);
        }

        // API 호출하여 데이터 가져오기
        List<Map<String, Object>> holidays = fetchHolidaysFromApi(year);
        
        // 데이터가 성공적으로 가져와졌다면 캐시에 저장
        if (!holidays.isEmpty()) {
            holidayCache.put(year, holidays);
        }

        return holidays;
    }

    private List<Map<String, Object>> fetchHolidaysFromApi(String year) {
        List<Map<String, Object>> holidays = new ArrayList<>();
        try {
            StringBuilder urlBuilder = new StringBuilder(API_URL);
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + SERVICE_KEY); // 서비스키는 이미 인코딩된 경우가 많음. 만약 안되면 인코딩 빼고 시도.
            urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            String xmlResponse = sb.toString();
            holidays = parseXml(xmlResponse);

        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 빈 리스트 반환 (로그 남기는 것이 좋음)
        }
        return holidays;
    }

    private List<Map<String, Object>> parseXml(String xml) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            
            NodeList items = doc.getElementsByTagName("item");
            
            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    String dateName = getTagValue("dateName", element);
                    String locdate = getTagValue("locdate", element); // YYYYMMDD
                    String isHoliday = getTagValue("isHoliday", element);

                    if ("Y".equals(isHoliday)) {
                        String formattedDate = locdate.substring(0, 4) + "-" + locdate.substring(4, 6) + "-" + locdate.substring(6, 8);
                        
                        Map<String, Object> event = new HashMap<>();
                        event.put("title", dateName);
                        event.put("start", formattedDate);
                        event.put("allDay", true);
                        event.put("display", "block");
                        event.put("backgroundColor", "transparent");
                        event.put("borderColor", "transparent");
                        event.put("textColor", "#dc3545"); // Red
                        event.put("classNames", List.of("holiday-event"));
                        
                        list.add(event);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
