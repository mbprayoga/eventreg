package com.example.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.EventKonser;
import com.example.model.EventSeminar;
import com.example.model.Pengguna;
import com.example.model.Penyelenggara;
import com.example.model.EventLomba;

@Controller
public class EventController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        try {
            // Parse as LocalDateTime first
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Format as LocalDate (to remove time component) and then format as dd-MM-yyyy
            return dateTime.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException e) {
            e.printStackTrace(); // Handle the parsing exception as needed
            return ""; // Return empty string or handle error case
        }
    }

    @GetMapping("/event/{idPengguna}")
    public String eventIndex(@PathVariable Long idPengguna, Model model) {
        String sql = "SELECT id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, jenislomba, gueststar, pembicara, tema, event "
                   + "FROM ("
                   + "    SELECT id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, jenislomba, NULL AS gueststar, NULL AS pembicara, NULL AS tema, 'lomba' AS event FROM eventlomba "
                   + "    UNION ALL "
                   + "    SELECT id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, NULL AS jenislomba, gueststar, NULL AS pembicara, NULL AS tema, 'konser' AS event FROM eventkonser "
                   + "    UNION ALL "
                   + "    SELECT id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, NULL AS jenislomba, NULL AS gueststar, pembicara, tema, 'seminar' AS event FROM eventseminar "
                   + ") events "
                   + "WHERE tanggal >= CURRENT_DATE "
                   + "ORDER BY tanggal ASC";

        List<Map<String, Object>> events = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> event = new HashMap<>();
                event.put("id", rs.getLong("id"));
                event.put("nama", rs.getString("nama"));
                event.put("tanggal", formatDate(rs.getString("tanggal")));
                event.put("lokasi", rs.getString("lokasi"));
                event.put("deskripsi", rs.getString("deskripsi"));
                event.put("biaya", rs.getInt("biaya"));
                event.put("idPenyelenggara", rs.getLong("idPenyelenggara"));
                event.put("jenislomba", rs.getString("jenislomba"));
                event.put("gueststar", rs.getString("gueststar"));
                event.put("pembicara", rs.getString("pembicara"));
                event.put("tema", rs.getString("tema"));
                event.put("event", rs.getString("event"));
                return event;
            }
        });

        model.addAttribute("events", events);
        model.addAttribute("idPenyelenggara", idPengguna);
        return "event";
    }

    @GetMapping("/konser/detail/{idPengguna}/{idEvent}")
    public String konserDetail(@PathVariable Long idPengguna, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT * FROM eventkonser WHERE id=?";
        EventKonser eventkonser = jdbcTemplate.queryForObject(sql, new Object[]{idEvent}, BeanPropertyRowMapper.newInstance(EventKonser.class));
        model.addAttribute("eventkonser", eventkonser);
        model.addAttribute("idPengguna", idPengguna);
        model.addAttribute("idEvent", idEvent);
        return "konserDetail";
    }

    @GetMapping("/lomba/detail/{idPengguna}/{idEvent}")
    public String lombaDetail(@PathVariable Long idPengguna, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT * FROM eventlomba WHERE id=?";
        EventLomba eventlomba = jdbcTemplate.queryForObject(sql, new Object[]{idEvent}, BeanPropertyRowMapper.newInstance(EventLomba.class));
        model.addAttribute("eventlomba", eventlomba);
        model.addAttribute("idPengguna", idPengguna);
        model.addAttribute("idEvent", idEvent);
        return "lombaDetail";
    }

    @GetMapping("/seminar/detail/{idPengguna}/{idEvent}")
    public String seminarDetail(@PathVariable Long idPengguna, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT * FROM eventseminar WHERE id=?";
        EventSeminar eventseminar = jdbcTemplate.queryForObject(sql, new Object[]{idEvent}, BeanPropertyRowMapper.newInstance(EventSeminar.class));
        model.addAttribute("eventseminar", eventseminar);
        model.addAttribute("idPengguna", idPengguna);
        model.addAttribute("idEvent", idEvent);
        return "seminarDetail";
    }

    @GetMapping("/konser/{idPenyelenggara}")
    public String konserIndex(@PathVariable Long idPenyelenggara, Model model) {
        String sql = "SELECT * FROM eventkonser WHERE idPenyelenggara = ?";
        List<EventKonser> eventkonser = jdbcTemplate.query(sql, new Object[]{idPenyelenggara}, new BeanPropertyRowMapper<>(EventKonser.class));
        model.addAttribute("eventkonser", eventkonser);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "konser";
    }

    @GetMapping("/seminar/{idPenyelenggara}")
    public String seminarIndex(@PathVariable Long idPenyelenggara, Model model) {
        String sql = "SELECT * FROM eventseminar WHERE idPenyelenggara = ?";
        List<EventSeminar> eventseminar = jdbcTemplate.query(sql, new Object[]{idPenyelenggara}, new BeanPropertyRowMapper<>(EventSeminar.class));
        model.addAttribute("eventseminar", eventseminar);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "seminar";
    }

    @GetMapping("/lomba/{idPenyelenggara}")
    public String lombaIndex(@PathVariable Long idPenyelenggara, Model model) {
        String sql = "SELECT * FROM eventlomba WHERE idPenyelenggara = ?";
        List<EventLomba> eventlomba = jdbcTemplate.query(sql, new Object[]{idPenyelenggara}, new BeanPropertyRowMapper<>(EventLomba.class));
        model.addAttribute("eventlomba", eventlomba);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "lomba";
    }

    @GetMapping("/event/add/{idPenyelenggara}")
    public String addEvent(@PathVariable Long idPenyelenggara, Model model) {
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "addEvent";
    }

    @GetMapping("/event/add/lomba/{idPenyelenggara}")
    public String addEventLomba(@PathVariable Long idPenyelenggara, Model model) {
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "addEventLomba";
    }

    @PostMapping("/event/add/lomba")
    public String addEventLomba(@RequestParam Long idPenyelenggara, EventLomba eventlomba) {
        String sql = "INSERT INTO eventlomba (id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, jenislomba) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, eventlomba.getId(), eventlomba.getNama(), eventlomba.getTanggal(), eventlomba.getLokasi(), 
                eventlomba.getDeskripsi(), eventlomba.getBiaya(), idPenyelenggara, eventlomba.getJenislomba());
        return "redirect:/lomba/" + idPenyelenggara;
    }

    @GetMapping("/event/add/seminar/{idPenyelenggara}")
    public String addEventSeminar(@PathVariable Long idPenyelenggara, Model model) {
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "addEventSeminar";
    }

    @PostMapping("/event/add/seminar")
    public String addEventSeminar(@RequestParam Long idPenyelenggara, EventSeminar eventseminar) {
        String sql = "INSERT INTO eventseminar (id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, pembicara, tema) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, eventseminar.getId(), eventseminar.getNama(), eventseminar.getTanggal(), eventseminar.getLokasi(), 
                eventseminar.getDeskripsi(), eventseminar.getBiaya(), idPenyelenggara, eventseminar.getPembicara(), eventseminar.getTema());
        return "redirect:/seminar/" + idPenyelenggara;
    }

    @GetMapping("/event/add/konser/{idPenyelenggara}")
    public String addEventKonser(@PathVariable Long idPenyelenggara, Model model) {
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        return "addEventKonser";
    }

    @PostMapping("/event/add/konser")
    public String addEventKonser(@RequestParam Long idPenyelenggara, EventKonser eventkonser) {
        String sql = "INSERT INTO eventkonser (id, nama, tanggal, lokasi, deskripsi, biaya, idPenyelenggara, gueststar) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, eventkonser.getId(), eventkonser.getNama(), eventkonser.getTanggal(), eventkonser.getLokasi(), 
                eventkonser.getDeskripsi(), eventkonser.getBiaya(), idPenyelenggara, eventkonser.getGueststar());
        return "redirect:/konser/" + idPenyelenggara;
    }
}

