package model;
import java.util.UUID;
public class Trabalhador {
    private UUID id;
    private String nome;
    private String matricula;
    private UUID filial_id;
    public Trabalhador() {}
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
    public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;}
    public String getMatricula(){return matricula;} public void setMatricula(String m){this.matricula=m;}
    public UUID getFilial_id(){return filial_id;} public void setFilial_id(UUID f){this.filial_id=f;}
}
