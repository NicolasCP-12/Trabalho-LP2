package entities;
import java.util.ArrayList;

public class Projetos {
    private String nome;
    private ArrayList<Tarefas> tarefas;
    private ArrayList<Alunos> equipa; // Substitui o HashMap que estava na UI

    public Projetos(String nome) {
        this.nome = nome;
        this.tarefas = new ArrayList<>();
        this.equipa = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public ArrayList<Tarefas> getTarefas() { return tarefas; }
    public void adicionarTarefa(Tarefas t) { if (!tarefas.contains(t)) tarefas.add(t); }
    public void removerTarefa(Tarefas t) { tarefas.remove(t); }

    public ArrayList<Alunos> getEquipa() { return equipa; }
    public void adicionarMembro(Alunos a) { if (!equipa.contains(a)) equipa.add(a); }
    public void removerMembro(Alunos a) { equipa.remove(a); }

    @Override
    public String toString() { return nome; }
}