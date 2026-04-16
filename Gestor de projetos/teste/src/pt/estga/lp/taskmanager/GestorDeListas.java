package pt.estga.lp.taskmanager;

import entities.Alunos;
import entities.Equipes;
import entities.Projetos;
import entities.Tarefas;

import java.util.ArrayList;

public class GestorDeListas {

    private ArrayList<Projetos> projetos = new ArrayList<>();
    private ArrayList<Tarefas> tarefas = new ArrayList<>();
    private ArrayList<Equipes> equipes = new ArrayList<>();
    private ArrayList<Alunos> alunos = new ArrayList<>();

    // Associações implementadas com listas paralelas (sem Map)
    // tarefaParaProjetoIndex.get(i) corresponde ao projeto associado a tarefaProjetoTarefas.get(i)
    private ArrayList<Tarefas> tarefaProjetoTarefas = new ArrayList<>();
    private ArrayList<Projetos> tarefaProjetoProjetos = new ArrayList<>();

    // tarefaParaAlunoIndex.get(i) corresponde ao aluno associado a tarefaAlunoTarefas.get(i)
    private ArrayList<Tarefas> tarefaAlunoTarefas = new ArrayList<>();
    private ArrayList<Alunos> tarefaAlunoAlunos = new ArrayList<>();

    // ---------------- Projetos ----------------
    public void criarProjeto(String nome) {
        projetos.add(new Projetos(nome));
    }

    public boolean editarProjeto(String nomeAntigo, String nomeNovo) {
        for (Projetos p : projetos) {
            if (p.getNome().equals(nomeAntigo)) {
                p.setNome(nomeNovo);
                return true;
            }
        }
        return false;
    }

    public boolean removerProjeto(String nome) {
        boolean removed = projetos.removeIf(p -> p.getNome().equals(nome));
        if (removed) {
            // remover associações tarefa->projeto que referenciem esse projeto
            for (int i = tarefaProjetoProjetos.size() - 1; i >= 0; i--) {
                Projetos proj = tarefaProjetoProjetos.get(i);
                if (proj.getNome().equals(nome)) {
                    tarefaProjetoProjetos.remove(i);
                    tarefaProjetoTarefas.remove(i);
                }
            }
        }
        return removed;
    }

    public ArrayList<Projetos> getProjetos() {
        return projetos;
    }

    // ---------------- Tarefas ----------------
    public void criarTarefas(String nome, String status) {
        tarefas.add(new Tarefas(nome, status));
    }

    public boolean editarTarefa(String nomeAntigo, String nomeNovo) {
        for (Tarefas t : tarefas) {
            if (t.getNome().equals(nomeAntigo)) {
                t.setNome(nomeNovo);
                return true;
            }
        }
        return false;
    }

    public boolean removerTarefas(String nome) {
        // encontrar tarefa e remover associações paralelas
        Tarefas alvo = null;
        for (Tarefas t : tarefas) {
            if (t.getNome().equals(nome)) {
                alvo = t;
                break;
            }
        }
        if (alvo != null) {
            tarefas.remove(alvo);
            // remover associações tarefa->projeto
            for (int i = tarefaProjetoTarefas.size() - 1; i >= 0; i--) {
                if (tarefaProjetoTarefas.get(i).equals(alvo)) {
                    tarefaProjetoTarefas.remove(i);
                    tarefaProjetoProjetos.remove(i);
                }
            }
            // remover associações tarefa->aluno
            for (int i = tarefaAlunoTarefas.size() - 1; i >= 0; i--) {
                if (tarefaAlunoTarefas.get(i).equals(alvo)) {
                    tarefaAlunoTarefas.remove(i);
                    tarefaAlunoAlunos.remove(i);
                }
            }
            return true;
        }
        return false;
    }

    public ArrayList<Tarefas> getTarefas() {
        return tarefas;
    }

    public boolean alterarStatusTarefa(String nomeTarefa, String novoStatus) {
        for (Tarefas t : tarefas) {
            if (t.getNome().equals(nomeTarefa)) {
                t.setStatus(novoStatus);
                return true;
            }
        }
        return false;
    }

    // associar tarefa a projeto (por nomes)
    public boolean associarTarefaAProjeto(String nomeTarefa, String nomeProjeto) {
        Tarefas tarefa = null;
        for (Tarefas t : tarefas) {
            if (t.getNome().equals(nomeTarefa)) {
                tarefa = t;
                break;
            }
        }
        Projetos projeto = null;
        for (Projetos p : projetos) {
            if (p.getNome().equals(nomeProjeto)) {
                projeto = p;
                break;
            }
        }
        if (tarefa != null && projeto != null) {
            // se já existir associação para essa tarefa, atualiza
            for (int i = 0; i < tarefaProjetoTarefas.size(); i++) {
                if (tarefaProjetoTarefas.get(i).equals(tarefa)) {
                    tarefaProjetoProjetos.set(i, projeto);
                    return true;
                }
            }
            // senão adiciona nova associação
            tarefaProjetoTarefas.add(tarefa);
            tarefaProjetoProjetos.add(projeto);
            return true;
        }
        return false;
    }

    public Projetos getProjetoDaTarefa(String nomeTarefa) {
        for (int i = 0; i < tarefaProjetoTarefas.size(); i++) {
            if (tarefaProjetoTarefas.get(i).getNome().equals(nomeTarefa)) {
                return tarefaProjetoProjetos.get(i);
            }
        }
        return null;
    }

    public ArrayList<Tarefas> getTarefasPorProjeto(String nomeProjeto) {
        ArrayList<Tarefas> res = new ArrayList<>();
        for (int i = 0; i < tarefaProjetoProjetos.size(); i++) {
            if (tarefaProjetoProjetos.get(i).getNome().equals(nomeProjeto)) {
                res.add(tarefaProjetoTarefas.get(i));
            }
        }
        return res;
    }

    // ---------------- Alunos ----------------
    public void criarAluno(String nome) {
        alunos.add(new Alunos(nome));
    }

    public boolean editarAluno(String nomeAntigo, String nomeNovo) {
        for (Alunos a : alunos) {
            if (a.getNome().equals(nomeAntigo)) {
                a.setNome(nomeNovo);
                return true;
            }
        }
        return false;
    }

    public boolean removerAluno(String nome) {
        boolean removed = alunos.removeIf(a -> a.getNome().equals(nome));
        if (removed) {
            // remover aluno de todas as equipas
            for (Equipes e : equipes) {
                e.getAlunos().removeIf(al -> al.getNome().equals(nome));
            }
            // remover associações tarefa->aluno
            for (int i = tarefaAlunoAlunos.size() - 1; i >= 0; i--) {
                if (tarefaAlunoAlunos.get(i).getNome().equals(nome)) {
                    tarefaAlunoAlunos.remove(i);
                    tarefaAlunoTarefas.remove(i);
                }
            }
        }
        return removed;
    }

    public ArrayList<Alunos> getAlunos() {
        return alunos;
    }

    // ---------------- Equipes ----------------
    public void criarEquipe() {
        equipes.add(new Equipes());
    }

    public boolean removerEquipe(int idEquipe) {
        return equipes.removeIf(e -> e.getId() == idEquipe);
    }

    public ArrayList<Equipes> getEquipes() {
        return equipes;
    }

    public boolean adicionarAlunoAEquipe(int idEquipe, String nomeAluno) {
        Equipes equipe = null;
        for (Equipes e : equipes) {
            if (e.getId() == idEquipe) {
                equipe = e;
                break;
            }
        }
        Alunos aluno = null;
        for (Alunos a : alunos) {
            if (a.getNome().equals(nomeAluno)) {
                aluno = a;
                break;
            }
        }
        if (equipe != null && aluno != null) {
            equipe.adicionarAluno(aluno);
            return true;
        }
        return false;
    }

    public boolean removerAlunoDeEquipe(int idEquipe, String nomeAluno) {
        for (Equipes e : equipes) {
            if (e.getId() == idEquipe) {
                e.removerAluno(nomeAluno);
                return true;
            }
        }
        return false;
    }

    // ---------------- Atribuição de tarefas a alunos (listas paralelas) ----------------
    public boolean atribuirTarefaAAluno(String nomeTarefa, String nomeAluno) {
        Tarefas tarefa = null;
        for (Tarefas t : tarefas) {
            if (t.getNome().equals(nomeTarefa)) {
                tarefa = t;
                break;
            }
        }
        Alunos aluno = null;
        for (Alunos a : alunos) {
            if (a.getNome().equals(nomeAluno)) {
                aluno = a;
                break;
            }
        }
        if (tarefa != null && aluno != null) {
            // se já existir associação para essa tarefa, atualiza
            for (int i = 0; i < tarefaAlunoTarefas.size(); i++) {
                if (tarefaAlunoTarefas.get(i).equals(tarefa)) {
                    tarefaAlunoAlunos.set(i, aluno);
                    return true;
                }
            }
            // senão adiciona nova associação
            tarefaAlunoTarefas.add(tarefa);
            tarefaAlunoAlunos.add(aluno);
            return true;
        }
        return false;
    }

    public boolean removerAtribuicaoTarefa(String nomeTarefa) {
        for (int i = tarefaAlunoTarefas.size() - 1; i >= 0; i--) {
            if (tarefaAlunoTarefas.get(i).getNome().equals(nomeTarefa)) {
                tarefaAlunoTarefas.remove(i);
                tarefaAlunoAlunos.remove(i);
                return true;
            }
        }
        return false;
    }

    public Alunos getAlunoAtribuidoATarefa(String nomeTarefa) {
        for (int i = 0; i < tarefaAlunoTarefas.size(); i++) {
            if (tarefaAlunoTarefas.get(i).getNome().equals(nomeTarefa)) {
                return tarefaAlunoAlunos.get(i);
            }
        }
        return null;
    }
}
