package entities;

public class Tarefas {
    private String nome;
    private String status; // "A Fazer", "Em Progresso", "Concluído"

    public Tarefas(String nome, String status) {
        this.nome = nome;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return nome + " [" + status + "]";
    }
}
