package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.model.Penyelenggara;

@Controller
public class PenyelenggaraController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/penyelenggara")
    public String penyelenggaraIndex(Model model) {
        String sql = "SELECT * FROM penyelenggara";
        List<Penyelenggara> penyelenggara = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Penyelenggara.class));
        model.addAttribute("penyelenggara", penyelenggara);
        return "penyelenggara";
    }

    @GetMapping("/penyelenggara/add")
    public String add(Model model) {
        return "addPenyelenggara";
    }

    @PostMapping("/penyelenggara/add")
    public String add(Penyelenggara penyelenggara) {
        String sql = "INSERT INTO penyelenggara VALUES(?,?,?,?,?)";
        jdbcTemplate.update(sql, penyelenggara.getId(),
                penyelenggara.getNama(), penyelenggara.getAlamat(), penyelenggara.getEmail(), penyelenggara.getPassword());
        return "redirect:/login/penyelenggara";
    }

    @GetMapping("/login/penyelenggara")
    public String loginPage() {
        return "loginPenyelenggara";
    }

    @PostMapping("/login/penyelenggara")
    public String login(@RequestParam("email") String email, 
                        @RequestParam("password") String password, 
                        Model model) {
        String sql = "SELECT * FROM penyelenggara WHERE email = ? AND password = ?";
        try {
            Penyelenggara penyelenggara = jdbcTemplate.queryForObject(
                sql,
                new Object[]{email, password},
                BeanPropertyRowMapper.newInstance(Penyelenggara.class)
            );
            model.addAttribute("penyelenggara", penyelenggara);
            return "redirect:/konser/"+ penyelenggara.getId(); // Redirect to a dashboard or another page after successful login
        } catch (EmptyResultDataAccessException e) {
            model.addAttribute("error", "Invalid email or password.");
            return "loginPenyelenggara"; // Stay on the login page and show error message
        } catch (IncorrectResultSizeDataAccessException e) {
            model.addAttribute("error", "Multiple users found with the same credentials. Please contact support.");
            return "loginPenyelenggara"; // Stay on the login page and show error message
        }
    }
}

