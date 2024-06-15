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
import com.example.model.Pengguna;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
//import javax.servlet.http.HttpServletRequest;

@Controller
public class PengunaController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/pengguna")
    public String penggunaIndex(Model model) {
        String sql = "SELECT * FROM pengguna";
        List<Pengguna> pengguna = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Pengguna.class));
        model.addAttribute("pengguna", pengguna);
        return "pengguna";
    }

    @GetMapping("/pengguna/add")
    public String add(Model model) {
        return "addPengguna";
    }

    @PostMapping("/pengguna/add")
    public String add(Pengguna pengguna) {
        String sql = "INSERT INTO pengguna VALUES(?,?,?,?)";
        jdbcTemplate.update(sql, pengguna.getId(),
                pengguna.getNama(), pengguna.getEmail(), pengguna.getPassword());
        return "redirect:/login/pengguna";
    }

    @GetMapping("/login/pengguna")
    public String loginPage() {
        return "loginPengguna";
    }

    
    @PostMapping("/login/pengguna")
    public String login(@RequestParam("email") String email, 
                        @RequestParam("password") String password, 
                        Model model) {
        String sql = "SELECT * FROM pengguna WHERE email = ? AND password = ?";
        try {
            Pengguna pengguna = jdbcTemplate.queryForObject(
                sql,
                new Object[]{email, password},
                BeanPropertyRowMapper.newInstance(Pengguna.class)
            );
            model.addAttribute("pengguna", pengguna);
            return "redirect:/event/" + pengguna.getId();
        } catch (EmptyResultDataAccessException e) {
            model.addAttribute("error", "Invalid email or password.");
            return "loginPengguna"; // Stay on the login page and show error message
        } catch (IncorrectResultSizeDataAccessException e) {
            model.addAttribute("error", "Multiple users found with the same credentials. Please contact support.");
            return "loginPengguna"; // Stay on the login page and show error message
        }
    }
}

