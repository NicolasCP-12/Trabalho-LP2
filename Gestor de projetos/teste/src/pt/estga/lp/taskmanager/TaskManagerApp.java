package pt.estga.lp.taskmanager;

import entities.Alunos;
import entities.Projetos;
import entities.Tarefas;
import trabalho2.IODataClass;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class TaskManagerApp extends JFrame {

    private GestorDeListas model = new GestorDeListas();

    private JList<Projetos> projectList;
    private DefaultListModel<Projetos> projectListModel;

    private JList<Tarefas> todoList;
    private JList<Tarefas> progressList;
    private JList<Tarefas> doneList;

    private DefaultListModel<Tarefas> todoModel;
    private DefaultListModel<Tarefas> progressModel;
    private DefaultListModel<Tarefas> doneModel;

    private JList<Alunos> teamList;
    private DefaultListModel<Alunos> teamModel;

    private JComboBox<Alunos> addMemberCombo;
    private JComboBox<Alunos> assignCombo;

    private JLabel assignedLabel;

    private Projetos currentProject;

    private static final String TODO = "A Fazer";
    private static final String PROGRESS = "Em Progresso";
    private static final String DONE = "Concluído";

    public TaskManagerApp() {
        initUI();
        loadData();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        setTitle("Gestor Kanban");
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Ficheiro");
        JMenuItem saveItem = new JMenuItem("Guardar");
        JMenuItem loadItem = new JMenuItem("Carregar");

        saveItem.addActionListener(e -> saveData());
        loadItem.addActionListener(e -> loadData());

        menu.add(saveItem);
        menu.add(loadItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // --- PAINEL ESQUERDO: PROJETOS ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Projetos"));
        leftPanel.setPreferredSize(new Dimension(200, 600));

        projectListModel = new DefaultListModel<>();
        projectList = new JList<>(projectListModel);
        projectList.addListSelectionListener(this::onProjectSelected);
        leftPanel.add(new JScrollPane(projectList), BorderLayout.CENTER);

        JPanel projectButtons = new JPanel(new GridLayout(3, 1));
        JButton addProjectBtn = new JButton("Novo");
        JButton editProjectBtn = new JButton("Editar");
        JButton deleteProjectBtn = new JButton("Eliminar");

        addProjectBtn.addActionListener(e -> addProject());
        editProjectBtn.addActionListener(e -> editProject());
        deleteProjectBtn.addActionListener(e -> deleteProject());

        projectButtons.add(addProjectBtn);
        projectButtons.add(editProjectBtn);
        projectButtons.add(deleteProjectBtn);
        leftPanel.add(projectButtons, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // --- PAINEL CENTRAL: TAREFAS (KANBAN) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Tarefas"));

        JPanel columnsPanel = new JPanel(new GridLayout(1, 3));

        todoModel = new DefaultListModel<>();
        todoList = new JList<>(todoModel);

        progressModel = new DefaultListModel<>();
        progressList = new JList<>(progressModel);

        doneModel = new DefaultListModel<>();
        doneList = new JList<>(doneModel);

        JPanel todoPanel = new JPanel(new BorderLayout());
        todoPanel.setBorder(BorderFactory.createTitledBorder(TODO));
        todoPanel.add(new JScrollPane(todoList), BorderLayout.CENTER);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder(PROGRESS));
        progressPanel.add(new JScrollPane(progressList), BorderLayout.CENTER);

        JPanel donePanel = new JPanel(new BorderLayout());
        donePanel.setBorder(BorderFactory.createTitledBorder(DONE));
        donePanel.add(new JScrollPane(doneList), BorderLayout.CENTER);

        columnsPanel.add(todoPanel);
        columnsPanel.add(progressPanel);
        columnsPanel.add(donePanel);

        centerPanel.add(columnsPanel, BorderLayout.CENTER);

        JPanel taskButtons = new JPanel(new GridLayout(1, 5));
        JButton addTaskBtn = new JButton("+ Tarefa");
        JButton editTaskBtn = new JButton("Editar");
        JButton deleteTaskBtn = new JButton("Remover");
        JButton leftBtn = new JButton("←");
        JButton rightBtn = new JButton("→");

        addTaskBtn.addActionListener(e -> addTask());
        editTaskBtn.addActionListener(e -> editTask());
        deleteTaskBtn.addActionListener(e -> deleteTask());
        leftBtn.addActionListener(e -> moveTask(-1));
        rightBtn.addActionListener(e -> moveTask(1));

        taskButtons.add(addTaskBtn);
        taskButtons.add(editTaskBtn);
        taskButtons.add(deleteTaskBtn);
        taskButtons.add(leftBtn);
        taskButtons.add(rightBtn);

        centerPanel.add(taskButtons, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- PAINEL DIREITO: EQUIPA ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Equipa do Projeto"));
        rightPanel.setPreferredSize(new Dimension(250, 600));

        assignedLabel = new JLabel("Atribuído a: ---");
        rightPanel.add(assignedLabel, BorderLayout.NORTH);

        teamModel = new DefaultListModel<>();
        teamList = new JList<>(teamModel);
        rightPanel.add(new JScrollPane(teamList), BorderLayout.CENTER);

        JPanel teamActions = new JPanel(new GridLayout(4, 1));

        addMemberCombo = new JComboBox<>();
        JButton addMemberBtn = new JButton("Adicionar membro");
        addMemberBtn.addActionListener(e -> addMember());

        JButton removeMemberBtn = new JButton("Remover membro");
        removeMemberBtn.addActionListener(e -> removeMember());

        JButton newStudentBtn = new JButton("Novo aluno no Sistema");
        newStudentBtn.addActionListener(e -> createStudent());

        assignCombo = new JComboBox<>();
        JButton assignBtn = new JButton("Atribuir tarefa");
        assignBtn.addActionListener(e -> assignTask());

        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(addMemberCombo, BorderLayout.CENTER);
        p1.add(addMemberBtn, BorderLayout.SOUTH);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(removeMemberBtn, BorderLayout.CENTER);

        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(newStudentBtn, BorderLayout.CENTER);

        JPanel p4 = new JPanel(new BorderLayout());
        p4.add(assignCombo, BorderLayout.CENTER);
        p4.add(assignBtn, BorderLayout.SOUTH);

        teamActions.add(p1);
        teamActions.add(p2);
        teamActions.add(p3);
        teamActions.add(p4);

        rightPanel.add(teamActions, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        // --- LISTENERS DE SELEÇÃO DE TAREFAS ---
        todoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && todoList.getSelectedValue() != null) {
                progressList.clearSelection();
                doneList.clearSelection();
                updateAssignedLabel();
            }
        });

        progressList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && progressList.getSelectedValue() != null) {
                todoList.clearSelection();
                doneList.clearSelection();
                updateAssignedLabel();
            }
        });

        doneList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && doneList.getSelectedValue() != null) {
                todoList.clearSelection();
                progressList.clearSelection();
                updateAssignedLabel();
            }
        });

        refreshProjectList();
    }

    private void msg(String text) {
        JOptionPane.showMessageDialog(this, text, "Mensagem", JOptionPane.PLAIN_MESSAGE);
    }

    private Tarefas getSelectedTask() {
        if (todoList.getSelectedValue() != null) return todoList.getSelectedValue();
        if (progressList.getSelectedValue() != null) return progressList.getSelectedValue();
        if (doneList.getSelectedValue() != null) return doneList.getSelectedValue();
        return null;
    }

    // --- ATUALIZAÇÃO DA INTERFACE ---

    private void refreshProjectList() {
        projectListModel.clear();
        for (Projetos p : model.getProjetos()) {
            projectListModel.addElement(p);
        }

        if (projectListModel.size() > 0 && currentProject == null) {
            projectList.setSelectedIndex(0);
        } else if (projectListModel.size() == 0) {
            currentProject = null;
            clearKanban();
            teamModel.clear();
            addMemberCombo.removeAllItems();
            assignCombo.removeAllItems();
            assignedLabel.setText("Atribuído a: ---");
        }
    }

    private void onProjectSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            currentProject = projectList.getSelectedValue();
            refreshKanban();
            refreshTeam();
            refreshAssignCombo();
            updateAssignedLabel();
        }
    }

    private void refreshKanban() {
        clearKanban();
        if (currentProject == null) return;

        for (Tarefas t : currentProject.getTarefas()) {
            if (t.getStatus().equals(TODO)) todoModel.addElement(t);
            else if (t.getStatus().equals(PROGRESS)) progressModel.addElement(t);
            else if (t.getStatus().equals(DONE)) doneModel.addElement(t);
        }
    }

    private void clearKanban() {
        todoModel.clear();
        progressModel.clear();
        doneModel.clear();
    }

    private void refreshTeam() {
        teamModel.clear();
        addMemberCombo.removeAllItems();

        if (currentProject == null) return;

        // Adiciona membros do projeto à lista da equipa
        for (Alunos a : currentProject.getEquipa()) {
            teamModel.addElement(a);
        }

        // Adiciona alunos globais que ainda NÃO estão na equipa à ComboBox
        for (Alunos a : model.getAlunos()) {
            if (!currentProject.getEquipa().contains(a)) {
                addMemberCombo.addItem(a);
            }
        }
    }

    private void refreshAssignCombo() {
        assignCombo.removeAllItems();
        if (currentProject == null) return;

        for (Alunos a : currentProject.getEquipa()) {
            assignCombo.addItem(a);
        }
    }

    private void updateAssignedLabel() {
        Tarefas task = getSelectedTask();
        if (task == null) {
            assignedLabel.setText("Atribuído a: ---");
            return;
        }

        Alunos a = task.getResponsavel();
        if (a == null) assignedLabel.setText("Atribuído a: Não atribuída");
        else assignedLabel.setText("Atribuído a: " + a.getNome());
    }

    // --- AÇÕES DOS BOTÕES ---

    private void addProject() {
        String name = JOptionPane.showInputDialog(this, "Nome do projeto:");
        if (name != null && !name.trim().isEmpty()) {
            model.criarProjeto(name.trim());
            refreshProjectList();
        }
    }

    private void editProject() {
        if (currentProject == null) {
            msg("Selecione um projeto.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Novo nome:", currentProject.getNome());
        if (newName != null && !newName.trim().isEmpty()) {
            currentProject.setNome(newName.trim()); // Atualização direta no objeto
            refreshProjectList();
        }
    }

    private void deleteProject() {
        if (currentProject == null) {
            msg("Selecione um projeto.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar projeto '" + currentProject.getNome() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.getProjetos().remove(currentProject);
            currentProject = null;
            refreshProjectList();
        }
    }

    private void addTask() {
        if (currentProject == null) {
            msg("Selecione um projeto.");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Nome da tarefa:");
        if (name != null && !name.trim().isEmpty()) {
            model.criarTarefa(name.trim(), TODO);
            model.associarTarefaAProjeto(name.trim(), currentProject.getNome());
            refreshKanban();
        }
    }

    private void editTask() {
        Tarefas t = getSelectedTask();
        if (t == null) {
            msg("Selecione uma tarefa.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Novo nome:", t.getNome());
        if (newName != null && !newName.trim().isEmpty()) {
            t.setNome(newName.trim());
            refreshKanban();
            updateAssignedLabel();
        }
    }

    private void deleteTask() {
        Tarefas t = getSelectedTask();
        if (t == null) {
            msg("Selecione uma tarefa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Remover tarefa '" + t.getNome() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentProject.removerTarefa(t);
            model.getTarefas().remove(t);
            refreshKanban();
            updateAssignedLabel();
        }
    }

    private void moveTask(int direction) {
        Tarefas t = getSelectedTask();
        if (t == null) {
            msg("Selecione uma tarefa.");
            return;
        }

        String newStatus = null;
        if (direction == -1) {
            if (t.getStatus().equals(PROGRESS)) newStatus = TODO;
            else if (t.getStatus().equals(DONE)) newStatus = PROGRESS;
        } else {
            if (t.getStatus().equals(TODO)) newStatus = PROGRESS;
            else if (t.getStatus().equals(PROGRESS)) newStatus = DONE;
        }

        if (newStatus != null) {
            t.setStatus(newStatus);
            refreshKanban();
            updateAssignedLabel();
        }
    }

    private void createStudent() {
        String name = JOptionPane.showInputDialog(this, "Nome do aluno:");
        if (name != null && !name.trim().isEmpty()) {
            model.criarAluno(name.trim());
            refreshTeam();
        }
    }

    private void addMember() {
        if (currentProject == null) {
            msg("Selecione um projeto.");
            return;
        }

        Alunos a = (Alunos) addMemberCombo.getSelectedItem();
        if (a == null) {
            msg("Não há alunos disponíveis.");
            return;
        }

        currentProject.adicionarMembro(a);
        refreshTeam();
        refreshAssignCombo();
    }

    private void removeMember() {
        if (currentProject == null) {
            msg("Selecione um projeto.");
            return;
        }

        Alunos a = teamList.getSelectedValue();
        if (a == null) {
            msg("Selecione um membro.");
            return;
        }

        currentProject.removerMembro(a);

        // Se a tarefa estiver atribuída a este aluno, removemos a atribuição
        for (Tarefas t : currentProject.getTarefas()) {
            if (t.getResponsavel() != null && t.getResponsavel().equals(a)) {
                t.setResponsavel(null);
            }
        }

        refreshTeam();
        refreshAssignCombo();
        refreshKanban();
        updateAssignedLabel();
    }

    private void assignTask() {
        Tarefas task = getSelectedTask();
        if (task == null) {
            msg("Selecione uma tarefa.");
            return;
        }

        Alunos a = (Alunos) assignCombo.getSelectedItem();
        if (a == null) {
            msg("A equipa não tem membros.");
            return;
        }

        task.setResponsavel(a);
        updateAssignedLabel();
        msg("Tarefa atribuída a " + a.getNome());
    }

    // --- PERSISTÊNCIA DE DADOS (Adaptada para objetos diretos) ---

    private void saveData() {
        try {
            IODataClass io = new IODataClass();
            ArrayList<String> linhas = new ArrayList<>();

            linhas.add("PROJETOS");
            for (Projetos p : model.getProjetos()) linhas.add(p.getNome());
            linhas.add("FIM_PROJETOS");

            linhas.add("TAREFAS");
            for (Projetos p : model.getProjetos()) {
                for (Tarefas t : p.getTarefas()) {
                    linhas.add(t.getNome() + "|" + t.getStatus() + "|" + p.getNome());
                }
            }
            linhas.add("FIM_TAREFAS");

            linhas.add("ALUNOS");
            for (Alunos a : model.getAlunos()) linhas.add(a.getNome());
            linhas.add("FIM_ALUNOS");

            linhas.add("ATRIBUICOES");
            for (Tarefas t : model.getTarefas()) {
                if (t.getResponsavel() != null) {
                    linhas.add(t.getNome() + "|" + t.getResponsavel().getNome());
                }
            }
            linhas.add("FIM_ATRIBUICOES");

            linhas.add("EQUIPAS_PROJETO");
            for (Projetos p : model.getProjetos()) {
                if (!p.getEquipa().isEmpty()) {
                    StringBuilder sb = new StringBuilder(p.getNome());
                    for (Alunos a : p.getEquipa()) {
                        sb.append("|").append(a.getNome());
                    }
                    linhas.add(sb.toString());
                }
            }
            linhas.add("FIM_EQUIPAS_PROJETO");

            io.writeData("kanban_data.txt", linhas.toArray(new String[0]));
            msg("Dados guardados com sucesso.");
        } catch (Exception e) {
            msg("Erro ao guardar: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File("kanban_data.txt");
        if (!file.exists()) return;

        try {
            IODataClass io = new IODataClass();
            String[] linhas = io.loadData("kanban_data.txt");
            if (linhas == null || linhas.length == 0) return;

            GestorDeListas newModel = new GestorDeListas();
            int i = 0;

            // 1. Carregar Projetos
            while (i < linhas.length && !linhas[i].equals("PROJETOS")) i++;
            if (i < linhas.length) i++;
            while (i < linhas.length && !linhas[i].equals("FIM_PROJETOS")) {
                if (!linhas[i].isEmpty()) newModel.criarProjeto(linhas[i]);
                i++;
            }

            // 2. Carregar Tarefas
            while (i < linhas.length && !linhas[i].equals("TAREFAS")) i++;
            if (i < linhas.length) i++;
            while (i < linhas.length && !linhas[i].equals("FIM_TAREFAS")) {
                if (!linhas[i].isEmpty()) {
                    String[] parts = linhas[i].split("\\|");
                    if (parts.length >= 2) {
                        newModel.criarTarefa(parts[0], parts[1]);
                        if (parts.length >= 3 && !parts[2].isEmpty()) {
                            newModel.associarTarefaAProjeto(parts[0], parts[2]);
                        }
                    }
                }
                i++;
            }

            // 3. Carregar Alunos Globais
            while (i < linhas.length && !linhas[i].equals("ALUNOS")) i++;
            if (i < linhas.length) i++;
            while (i < linhas.length && !linhas[i].equals("FIM_ALUNOS")) {
                if (!linhas[i].isEmpty()) newModel.criarAluno(linhas[i]);
                i++;
            }

            // 4. Carregar Atribuições de Tarefas
            while (i < linhas.length && !linhas[i].equals("ATRIBUICOES")) i++;
            if (i < linhas.length) i++;
            while (i < linhas.length && !linhas[i].equals("FIM_ATRIBUICOES")) {
                if (!linhas[i].isEmpty()) {
                    String[] parts = linhas[i].split("\\|");
                    if (parts.length == 2) newModel.atribuirTarefaAAluno(parts[0], parts[1]);
                }
                i++;
            }

            // 5. Carregar Equipas dos Projetos (Agora é ArrayList no Projeto)
            while (i < linhas.length && !linhas[i].equals("EQUIPAS_PROJETO")) i++;
            if (i < linhas.length) i++;
            while (i < linhas.length && !linhas[i].equals("FIM_EQUIPAS_PROJETO")) {
                if (!linhas[i].isEmpty()) {
                    String[] parts = linhas[i].split("\\|");
                    if (parts.length > 1) {
                        // Procurar o projeto
                        Projetos p = null;
                        for (Projetos proj : newModel.getProjetos()) {
                            if (proj.getNome().equals(parts[0])) {
                                p = proj;
                                break;
                            }
                        }
                        // Se encontrou o projeto, adiciona-lhe os membros
                        if (p != null) {
                            for (int j = 1; j < parts.length; j++) {
                                for (Alunos a : newModel.getAlunos()) {
                                    if (a.getNome().equals(parts[j])) {
                                        p.adicionarMembro(a);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                i++;
            }

            model = newModel;
            currentProject = null;
            refreshProjectList();

            msg("Dados carregados com sucesso.");
        } catch (Exception e) {
            msg("Erro ao carregar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskManagerApp::new);
    }
}