package model;
import java.util.UUID;
public class Ferramenta {
    private UUID id;
    private String identificacao;
    private String status;
    private UUID filial_id;
    private UUID trabalhador_id;
    public Ferramenta() {}
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
    public String getIdentificacao(){return identificacao;} public void setIdentificacao(String i){this.identificacao=i;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public UUID getFilial_id(){return filial_id;} public void setFilial_id(UUID f){this.filial_id=f;}
    public UUID getTrabalhador_id(){return trabalhador_id;} public void setTrabalhador_id(UUID t){this.trabalhador_id=t;}
    @Override public String toString(){ return identificacao + (id==null? "":" ("+id.toString()+")"); }
}
