package entities;

import java.util.ArrayList;

public class Equipes {

    private static int proximoId = 1; // contador global de IDs

    private int id;
    private ArrayList<Alunos> alunos = new ArrayList<>();

    public Equipes() {
        this.id = proximoId++;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Alunos> getAlunos() {
        return alunos;
    }

    public void adicionarAluno(Alunos aluno) {
        if (!alunos.contains(aluno)) alunos.add(aluno);
    }

    public void removerAluno(String nomeAluno) {
        alunos.removeIf(a -> a.getNome().equals(nomeAluno));
    }

    @Override
    public String toString() {
        return "Equipe " + id;
    }
}
