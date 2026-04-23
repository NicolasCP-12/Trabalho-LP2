package pt.estga.lp.taskmanager;

import entities.*;
import java.util.ArrayList;

public class GestorDeListas {

    // Listas mestras
    private ArrayList<Projetos> projetos = new ArrayList<>();
    private ArrayList<Tarefas> tarefas = new ArrayList<>();
    private ArrayList<Equipes> equipes = new ArrayList<>();
    private ArrayList<Alunos> alunos = new ArrayList<>();

    // --- Métodos de Criação ---
    public void criarProjeto(String nome) { projetos.add(new Projetos(nome)); }
    public void criarTarefa(String nome, String status) { tarefas.add(new Tarefas(nome, status)); }
    public void criarAluno(String nome) { alunos.add(new Alunos(nome)); }
    public void criarEquipe() { equipes.add(new Equipes()); }

    // --- Métodos de Associação ---
    public void associarTarefaAProjeto(String nomeTarefa, String nomeProjeto) {
        Tarefas t = encontrarTarefa(nomeTarefa);
        Projetos p = encontrarProjeto(nomeProjeto);
        if (t != null && p != null) p.adicionarTarefa(t);
    }

    public void atribuirTarefaAAluno(String nomeTarefa, String nomeAluno) {
        Tarefas t = encontrarTarefa(nomeTarefa);
        Alunos a = encontrarAluno(nomeAluno);
        if (t != null && a != null) t.setResponsavel(a);
    }

    public void moverTarefa(String nomeTarefa, String novoStatus) {
        Tarefas t = encontrarTarefa(nomeTarefa);
        if (t != null) t.setStatus(novoStatus);
    }

    // --- Métodos de Busca (Auxiliares) ---
    private Tarefas encontrarTarefa(String nome) {
        for (Tarefas t : tarefas) if (t.getNome().equals(nome)) return t;
        return null;
    }

    private Projetos encontrarProjeto(String nome) {
        for (Projetos p : projetos) if (p.getNome().equals(nome)) return p;
        return null;
    }

    private Alunos encontrarAluno(String nome) {
        for (Alunos a : alunos) if (a.getNome().equals(nome)) return a;
        return null;
    }

    // --- Getters para a Interface (View) ---
    public ArrayList<Projetos> getProjetos() { return projetos; }
    public ArrayList<Tarefas> getTarefas() { return tarefas; }
    public ArrayList<Alunos> getAlunos() { return alunos; }
    public ArrayList<Equipes> getEquipes() { return equipes; }
}