package model;
import java.util.UUID;
public class Filial {
    private UUID id;
    private String nome;
    private String local;
    public Filial() {}
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
    public String getNome(){return nome;} public void setNome(String nome){this.nome=nome;}
    public String getLocal(){return local;} public void setLocal(String local){this.local=local;}
    @Override public String toString(){return nome + (id==null? "":" ("+id.toString()+")");}
}
