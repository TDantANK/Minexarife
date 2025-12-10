package model;
import java.time.OffsetDateTime;
public class HistoricoTransferencia {
    private String detalhe;
    private OffsetDateTime momento;
    public HistoricoTransferencia() {}
    public String getDetalhe(){return detalhe;} public void setDetalhe(String d){this.detalhe=d;}
    public OffsetDateTime getMomento(){return momento;} public void setMomento(OffsetDateTime m){this.momento=m;}
}
