package me.dio.sdw24.domain.model;

public record Champions(
        Long id,
        String name,
        String role,
        String lore,
        String imageUrl
) {
    public String generateContextByQuestion(String question){
        return """
        Question: %s
        Champion: %s
        Role: %s
        Lore: %s""".formatted(question, this.name, this.role, this.lore);
    }
}
