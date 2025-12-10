package model;
import java.util.UUID;
public class Veiculo {
    private UUID id;
    private String placa;
    private UUID filial_id;
    private UUID responsavel_id;
    public Veiculo() {}
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
    public String getPlaca(){return placa;} public void setPlaca(String p){this.placa=p;}
    public UUID getFilial_id(){return filial_id;} public void setFilial_id(UUID f){this.filial_id=f;}
    public UUID getResponsavel_id(){return responsavel_id;} public void setResponsavel_id(UUID r){this.responsavel_id=r;}
}
