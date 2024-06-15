package com.example.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.EventKonser;
import com.example.model.Tiket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Controller
public class TiketController {
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

    @GetMapping("/tiket/{idPengguna}")
    public String konserIndex(@PathVariable Long idPengguna, Model model) {
        String sql = "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                    "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe, e.tanggal AS tanggal, " +
                    "a.id AS asuransi, pkr.id AS parkir, pkr.noParkir AS no_parkir " +
                    "FROM tiket t " +
                    "JOIN eventkonser e ON e.id = t.idEvent " +
                    "JOIN pengguna p ON t.idPengguna = p.id " +
                    "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                    "JOIN asuransi a ON t.id = a.idTiket " +
                    "JOIN parkir pkr ON t.id = pkr.idTiket " +
                    "WHERE t.tipe = 'eventkonser' AND p.id = ? " +
                    "UNION " +
                    "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                    "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe, e.tanggal AS tanggal, " +
                    "a.id AS asuransi, pkr.id AS parkir, pkr.noParkir AS no_parkir " +
                    "FROM tiket t " +
                    "JOIN eventlomba e ON e.id = t.idEvent " +
                    "JOIN pengguna p ON t.idPengguna = p.id " +
                    "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                    "JOIN asuransi a ON t.id = a.idTiket " +
                    "JOIN parkir pkr ON t.id = pkr.idTiket " +
                    "WHERE t.tipe = 'eventlomba' AND p.id = ? " +
                    "UNION " +
                    "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                    "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe, e.tanggal AS tanggal, " +
                    "a.id AS asuransi, pkr.id AS parkir, pkr.noParkir AS no_parkir " +
                    "FROM tiket t " +
                    "JOIN eventseminar e ON e.id = t.idEvent " +
                    "JOIN pengguna p ON t.idPengguna = p.id " +
                    "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                    "JOIN asuransi a ON t.id = a.idTiket " +
                    "JOIN parkir pkr ON t.id = pkr.idTiket " +
                    "WHERE t.tipe = 'eventseminar' AND p.id = ? ";

        List<Map<String, Object>> tiketMaps = jdbcTemplate.query(sql, new Object[]{idPengguna, idPengguna, idPengguna}, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> tiketMap = new HashMap<>();
                tiketMap.put("event_nama", rs.getString("event_nama"));
                tiketMap.put("pengguna_nama", rs.getString("pengguna_nama"));
                tiketMap.put("pengguna_email", rs.getString("pengguna_email"));
                tiketMap.put("penyelenggara_nama", rs.getString("penyelenggara_nama"));
                tiketMap.put("tiket_id", rs.getLong("tiket_id"));
                tiketMap.put("tiket_tipe", rs.getString("tiket_tipe"));
                tiketMap.put("tanggal", formatDate(rs.getString("tanggal")));
                tiketMap.put("asuransi", rs.getString("asuransi"));
                tiketMap.put("parkir", rs.getString("parkir"));
                tiketMap.put("no_parkir", rs.getString("no_parkir"));
                return tiketMap;
            }
        });

        model.addAttribute("tiketMaps", tiketMaps);
        model.addAttribute("idPengguna", idPengguna);
        return "tiketkonser";
    }

    @GetMapping("/konser/peserta/{idPenyelenggara}/{idEvent}")
    public String pesertaKonserIndex(@PathVariable Long idPenyelenggara, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                     "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe " +
                     "FROM tiket t " +
                     "JOIN eventkonser e ON e.id = t.idEvent " +
                     "JOIN pengguna p ON t.idPengguna = p.id " +
                     "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                     "WHERE t.tipe = 'eventkonser' AND e.idPenyelenggara = ? AND t.idEvent = ?" +
                     "ORDER BY e.tanggal";        
        List<Map<String, Object>> tiketMaps = jdbcTemplate.query(sql, new Object[]{idPenyelenggara, idEvent}, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> tiketMap = new HashMap<>();
                tiketMap.put("event_nama", rs.getString("event_nama"));
                tiketMap.put("pengguna_nama", rs.getString("pengguna_nama"));
                tiketMap.put("pengguna_email", rs.getString("pengguna_email"));
                tiketMap.put("penyelenggara_nama", rs.getString("penyelenggara_nama"));
                tiketMap.put("tiket_id", rs.getLong("tiket_id"));
                tiketMap.put("tiket_tipe", rs.getString("tiket_tipe"));
                return tiketMap;
            }
        });

        model.addAttribute("tiketMaps", tiketMaps);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        model.addAttribute("idEvent", idEvent);
        return "konserPeserta";
    }

    @GetMapping("/lomba/peserta/{idPenyelenggara}/{idEvent}")
    public String pesertaLombaIndex(@PathVariable Long idPenyelenggara, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                     "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe " +
                     "FROM tiket t " +
                     "JOIN eventlomba e ON e.id = t.idEvent " +
                     "JOIN pengguna p ON t.idPengguna = p.id " +
                     "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                     "WHERE t.tipe = 'eventlomba' AND e.idPenyelenggara = ? AND t.idEvent = ?" +
                     "ORDER BY e.tanggal";        
        List<Map<String, Object>> tiketMaps = jdbcTemplate.query(sql, new Object[]{idPenyelenggara, idEvent}, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> tiketMap = new HashMap<>();
                tiketMap.put("event_nama", rs.getString("event_nama"));
                tiketMap.put("pengguna_nama", rs.getString("pengguna_nama"));
                tiketMap.put("pengguna_email", rs.getString("pengguna_email"));
                tiketMap.put("penyelenggara_nama", rs.getString("penyelenggara_nama"));
                tiketMap.put("tiket_id", rs.getLong("tiket_id"));
                tiketMap.put("tiket_tipe", rs.getString("tiket_tipe"));
                return tiketMap;
            }
        });

        model.addAttribute("tiketMaps", tiketMaps);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        model.addAttribute("idEvent", idEvent);
        return "lombaPeserta";
    }

    @GetMapping("/seminar/peserta/{idPenyelenggara}/{idEvent}")
    public String pesertaIndex(@PathVariable Long idPenyelenggara, @PathVariable Long idEvent, Model model) {
        String sql = "SELECT e.nama AS event_nama, p.nama AS pengguna_nama, p.email AS pengguna_email, " +
                     "peny.nama AS penyelenggara_nama, t.id AS tiket_id, t.tipe AS tiket_tipe " +
                     "FROM tiket t " +
                     "JOIN eventseminar e ON e.id = t.idEvent " +
                     "JOIN pengguna p ON t.idPengguna = p.id " +
                     "JOIN penyelenggara peny ON e.idPenyelenggara = peny.id " +
                     "WHERE t.tipe = 'eventseminar' AND e.idPenyelenggara = ? AND t.idEvent = ?" +
                     "ORDER BY e.tanggal";        
        List<Map<String, Object>> tiketMaps = jdbcTemplate.query(sql, new Object[]{idPenyelenggara, idEvent}, new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, Object> tiketMap = new HashMap<>();
                tiketMap.put("event_nama", rs.getString("event_nama"));
                tiketMap.put("pengguna_nama", rs.getString("pengguna_nama"));
                tiketMap.put("pengguna_email", rs.getString("pengguna_email"));
                tiketMap.put("penyelenggara_nama", rs.getString("penyelenggara_nama"));
                tiketMap.put("tiket_id", rs.getLong("tiket_id"));
                tiketMap.put("tiket_tipe", rs.getString("tiket_tipe"));
                return tiketMap;
            }
        });

        model.addAttribute("tiketMaps", tiketMaps);
        model.addAttribute("idPenyelenggara", idPenyelenggara);
        model.addAttribute("idEvent", idEvent);
        return "seminarPeserta";
    }

    @PostMapping("/lomba/register/{idPengguna}/{idEvent}")
    public String registerLomba(@PathVariable Long idPengguna, 
                                @PathVariable Long idEvent,
                                @RequestParam(required = false) Boolean parkir,
                                @RequestParam(required = false) Boolean asuransi) {

        // KeyHolder for retrieving the generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert into tiket table and retrieve the generated ID
        String tiketSql = "INSERT INTO tiket (id, idEvent, idPengguna, tipe) VALUES (TIKET_SEQ.NEXTVAL, ?, ?, ?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(tiketSql, new String[]{"id"});
                ps.setLong(1, idEvent);
                ps.setLong(2, idPengguna);
                ps.setString(3, "eventlomba");
                return ps;
            }
        }, keyHolder);

        Long tiketId = keyHolder.getKey().longValue();
        System.out.println("Generated tiketId: " + tiketId); // Log the generated tiketId for debugging

        // If asuransi is selected, insert into asuransi table
        if (asuransi != null && asuransi) {
            String asuransiSql = "INSERT INTO asuransi (id, biaya, idTiket) VALUES (ASURANSI_SEQ.NEXTVAL, ?, ?)";
            jdbcTemplate.update(asuransiSql, 20000, tiketId);
            System.out.println("Inserted asuransi with biaya: 20000, idTiket: " + tiketId); // Log for debugging
        }

        // If parkir is selected, insert into parkir table
        if (parkir != null && parkir) {
            // Generate noParkir value
            String noParkir = generateNoParkir();
            String parkirSql = "INSERT INTO parkir (id, biaya, noParkir, idTiket) VALUES (PARKIR_SEQ.NEXTVAL, ?, ?, ?)";
            jdbcTemplate.update(parkirSql, 5000, noParkir, tiketId);
            System.out.println("Inserted parkir with biaya: 5000, noParkir: " + noParkir + ", idTiket: " + tiketId); // Log for debugging
        }

        return "redirect:/tiket/" + idPengguna;
    }

    @PostMapping("/konser/register/{idPengguna}/{idEvent}")
    public String registerKonser(@PathVariable Long idPengguna, 
                                @PathVariable Long idEvent,
                                @RequestParam(required = false) Boolean parkir,
                                @RequestParam(required = false) Boolean asuransi) {

        // KeyHolder for retrieving the generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert into tiket table and retrieve the generated ID
        String tiketSql = "INSERT INTO tiket (id, idEvent, idPengguna, tipe) VALUES (TIKET_SEQ.NEXTVAL, ?, ?, ?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(tiketSql, new String[]{"id"});
                ps.setLong(1, idEvent);
                ps.setLong(2, idPengguna);
                ps.setString(3, "eventkonser");
                return ps;
            }
        }, keyHolder);

        Long tiketId = keyHolder.getKey().longValue();
        System.out.println("Generated tiketId: " + tiketId); // Log the generated tiketId for debugging

        // If asuransi is selected, insert into asuransi table
        if (asuransi != null && asuransi) {
            String asuransiSql = "INSERT INTO asuransi (id, biaya, idTiket) VALUES (ASURANSI_SEQ.NEXTVAL, ?, ?)";
            jdbcTemplate.update(asuransiSql, 20000, tiketId);
            System.out.println("Inserted asuransi with biaya: 20000, idTiket: " + tiketId); // Log for debugging
        }

        // If parkir is selected, insert into parkir table
        if (parkir != null && parkir) {
            // Generate noParkir value
            String noParkir = generateNoParkir();
            String parkirSql = "INSERT INTO parkir (id, biaya, noParkir, idTiket) VALUES (PARKIR_SEQ.NEXTVAL, ?, ?, ?)";
            jdbcTemplate.update(parkirSql, 5000, noParkir, tiketId);
            System.out.println("Inserted parkir with biaya: 5000, noParkir: " + noParkir + ", idTiket: " + tiketId); // Log for debugging
        }

        return "redirect:/tiket/" + idPengguna;
    }

    @PostMapping("/seminar/register/{idPengguna}/{idEvent}")
    public String registerEvent(@PathVariable Long idPengguna, 
                                @PathVariable Long idEvent,
                                @RequestParam(required = false) Boolean parkir,
                                @RequestParam(required = false) Boolean asuransi) {

        // KeyHolder for retrieving the generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Insert into tiket table and retrieve the generated ID
        String tiketSql = "INSERT INTO tiket (id, idEvent, idPengguna, tipe) VALUES (TIKET_SEQ.NEXTVAL, ?, ?, ?)";
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(tiketSql, new String[]{"id"});
                ps.setLong(1, idEvent);
                ps.setLong(2, idPengguna);
                ps.setString(3, "eventseminar");
                return ps;
            }
        }, keyHolder);

        Long tiketId = keyHolder.getKey().longValue();
        System.out.println("Generated tiketId: " + tiketId); // Log the generated tiketId for debugging

        // If asuransi is selected, insert into asuransi table
        if (asuransi != null && asuransi) {
            String asuransiSql = "INSERT INTO asuransi (id, biaya, idTiket) VALUES (ASURANSI_SEQ.NEXTVAL, ?, ?)";
            jdbcTemplate.update(asuransiSql, 20000, tiketId);
            System.out.println("Inserted asuransi with biaya: 20000, idTiket: " + tiketId); // Log for debugging
        }

        // If parkir is selected, insert into parkir table
        if (parkir != null && parkir) {
            // Generate noParkir value
            String noParkir = generateNoParkir();
            String parkirSql = "INSERT INTO parkir (id, biaya, noParkir, idTiket) VALUES (PARKIR_SEQ.NEXTVAL, ?, ?, ?)";
            jdbcTemplate.update(parkirSql, 5000, noParkir, tiketId);
            System.out.println("Inserted parkir with biaya: 5000, noParkir: " + noParkir + ", idTiket: " + tiketId); // Log for debugging
        }

        return "redirect:/tiket/" + idPengguna;
    }

    private String generateNoParkir() {
        // Logic to generate the noParkir value from A0-Z9
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int letterIndex = (int) (Math.random() * characters.length());
        int number = (int) (Math.random() * 10);
        return characters.charAt(letterIndex) + Integer.toString(number);
    }
}

