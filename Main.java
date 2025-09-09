import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Usuario {
    String nome;
    String email;
    String cidade;

    public Usuario(String nome, String email, String cidade) {
        this.nome = nome;
        this.email = email;
        this.cidade = cidade;
    }

    public String toString() {
        return nome + " (" + email + ", " + cidade + ")";
    }
}

class Evento {
    String nome;
    String endereco;
    String categoria;
    LocalDateTime dataHora;
    String descricao;
    List<String> participantes = new ArrayList<>();

    public Evento(String nome, String endereco, String categoria, LocalDateTime dataHora, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public String toString() {
        return nome + " | " + categoria + " | " + dataHora + " | " + endereco + " | " + descricao;
    }

    public String toData() {
        return nome + ";" + endereco + ";" + categoria + ";" + dataHora + ";" + descricao + ";" + String.join(",", participantes);
    }

    public static Evento fromData(String linha) {
        String[] p = linha.split(";");
        Evento e = new Evento(p[0], p[1], p[2], LocalDateTime.parse(p[3]), p[4]);
        if (p.length > 5 && !p[5].isEmpty()) {
            e.participantes.addAll(Arrays.asList(p[5].split(",")));
        }
        return e;
    }
}

public class Main {
    static List<Evento> eventos = new ArrayList<>();
    static Usuario usuarioAtual;
    static Scanner sc = new Scanner(System.in);
    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static void main(String[] args) {
        carregarEventos();
        System.out.println("=== Sistema de Eventos ===");
        cadastrarUsuario();

        int opcao;
        do {
            System.out.println("\n1 - Cadastrar evento");
            System.out.println("2 - Listar eventos");
            System.out.println("3 - Participar de evento");
            System.out.println("4 - Cancelar participação");
            System.out.println("5 - Meus eventos");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {
                case 1: cadastrarEvento(); break;
                case 2: listarEventos(); break;
                case 3: participar(); break;
                case 4: cancelar(); break;
                case 5: meusEventos(); break;
            }
        } while (opcao != 0);

        salvarEventos();
        System.out.println("Até logo!");
    }

    static void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Cidade: ");
        String cidade = sc.nextLine();
        usuarioAtual = new Usuario(nome, email, cidade);
    }

    static void cadastrarEvento() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Endereço: ");
        String end = sc.nextLine();
        System.out.print("Categoria (Festa, Show, Esporte...): ");
        String cat = sc.nextLine();
        System.out.print("Data e hora (yyyy-MM-dd'T'HH:mm): ");
        LocalDateTime dt = LocalDateTime.parse(sc.nextLine(), fmt);
        System.out.print("Descrição: ");
        String desc = sc.nextLine();

        eventos.add(new Evento(nome, end, cat, dt, desc));
        System.out.println("Evento cadastrado!");
    }

    static void listarEventos() {
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        int i = 1;
        for (Evento e : eventos) {
            System.out.println(i++ + " - " + e);
        }
    }

    static void participar() {
        listarEventos();
        System.out.print("Escolha o número do evento: ");
        int n = Integer.parseInt(sc.nextLine());
        Evento e = eventos.get(n - 1);
        if (!e.participantes.contains(usuarioAtual.email)) {
            e.participantes.add(usuarioAtual.email);
            System.out.println("Você confirmou presença!");
        } else {
            System.out.println("Você já está participando.");
        }
    }

    static void cancelar() {
        listarEventos();
        System.out.print("Escolha o número do evento: ");
        int n = Integer.parseInt(sc.nextLine());
        Evento e = eventos.get(n - 1);
        if (e.participantes.remove(usuarioAtual.email)) {
            System.out.println("Participação cancelada.");
        } else {
            System.out.println("Você não estava participando desse evento.");
        }
    }

    static void meusEventos() {
        System.out.println("Eventos que você participa:");
        for (Evento e : eventos) {
            if (e.participantes.contains(usuarioAtual.email)) {
                System.out.println("- " + e.nome + " em " + e.dataHora);
            }
        }
    }

    static void salvarEventos() {
        try (PrintWriter pw = new PrintWriter("events.data")) {
            for (Evento e : eventos) {
                pw.println(e.toData());
            }
        } catch (Exception ex) {
            System.out.println("Erro ao salvar: " + ex.getMessage());
        }
    }

    static void carregarEventos() {
        try (BufferedReader br = new BufferedReader(new FileReader("events.data"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                eventos.add(Evento.fromData(linha));
            }
        } catch (Exception ex) {
            // se o arquivo não existir, começa vazio
        }
    }
}
