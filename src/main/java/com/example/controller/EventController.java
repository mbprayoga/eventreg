package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.EventKonser;
import com.example.model.EventSeminar;
import com.example.model.Penyelenggara;
import com.example.model.EventLomba;

@Controller
public class EventController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

