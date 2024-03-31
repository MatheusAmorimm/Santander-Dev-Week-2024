package me.dio.sdw24.adapters.out;

import me.dio.sdw24.domain.model.Champions;
import me.dio.sdw24.domain.ports.ChampionsRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChampionsJdbcRepository implements ChampionsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Champions> rowMapper;

    public ChampionsJdbcRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = ((resultSet, rowNum) -> new Champions(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("role"),
                resultSet.getString("lore"),
                resultSet.getString("image_url")
        ));
    }

    @Override
    public List<Champions> findAll() {
        return jdbcTemplate.query("Select * from champions", rowMapper);
    }

    @Override
    public Optional<Champions> findById(Long id) {
        String sql = "Select * from champions where id = ?";
        List<Champions> champions = jdbcTemplate.query(sql, rowMapper, id);
        return champions.stream().findFirst();
    }
}
