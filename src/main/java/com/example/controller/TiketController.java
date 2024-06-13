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
import com.example.model.Tiket;

@Controller
public class TiketController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/user/tiket")
    public String index(Model model) {
        String sql = "SELECT * FROM tiket";
        List<Tiket> tiket = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Tiket.class));
        model.addAttribute("tiket", tiket);
        return "index";
    }

}

