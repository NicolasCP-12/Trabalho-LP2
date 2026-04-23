package entities;
import java.util.ArrayList;

public class Equipes {
    private static int proximoId = 1;
    private int id;
    private ArrayList<Alunos> alunos;

    public Equipes() {
        this.id = proximoId++;
        this.alunos = new ArrayList<>();
    }

    public int getId() { return id; }
    public ArrayList<Alunos> getAlunos() { return alunos; }

    public void adicionarAluno(Alunos aluno) {
        if (!alunos.contains(aluno)) alunos.add(aluno);
    }

    public void removerAluno(Alunos aluno) {
        alunos.remove(aluno);
    }
}