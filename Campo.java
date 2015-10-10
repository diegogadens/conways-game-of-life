import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Random;
import java.lang.Runtime;
import java.awt.Color;



/**
 * Representa uma grade retangular das posições do campo.
 * Cada posição é capaz de armazenar um único animal.
 * 
 * @author David J. Barnes e Michael Kölling
 * @version 2006.03.30
 */
public class Campo
{
    private static final Random aleatório = new Random();
    
    // A profundidade e a largura do campo.
    private int profundidade, largura;
    // Um repositório para os animais.
    private Object[][] campox;
    public Celula[][] campo;
    public static final Color vivo = Color.BLACK;
    public static final Color morto = Color.WHITE;

    public Campo(int profundidade, int largura)
    {
        this.profundidade = profundidade;
        this.largura = largura;
        campo = new Celula[profundidade][largura];
        init();
    }
    
    public class Celula
    {
        Color status,statusNovo;
        int vizinhanca,x,y;
        public Celula(int x, int y)
        {
            this.x = x;
            this.y = y;
            status = morto;
        }
        public Color getStatus()
        {
            return status;
        }
        public void setStatus(Color status)
        {
            this.status = status;
        }
        public void proxEstado()
        {
            vizinhanca = 0;
            
            if(posicaoValida(x-1,y-1))
            {
                if(campo[x-1][y-1].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x-1,y))
            {
                if(campo[x-1][y].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x-1,y+1))
            {
                if(campo[x-1][y+1].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x,y-1))
            {
                if(campo[x][y-1].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x,y+1))
            {
                if(campo[x][y+1].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x+1,y-1))
            {
                if(campo[x+1][y-1].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x+1,y))
            {
                if(campo[x+1][y].getStatus() == vivo)
                vizinhanca += 1;
            }
            if(posicaoValida(x+1,y+1))
            {
                if(campo[x+1][y+1].getStatus() == vivo)
                vizinhanca += 1;
            }
            
            
            if(vizinhanca <= 1)
            statusNovo = morto;
            
            if((vizinhanca == 3) && status == morto)
            statusNovo = vivo;
            
            if((vizinhanca == 2) && status == vivo)
            statusNovo = vivo;
            
            if((vizinhanca == 3) && status == vivo)
            statusNovo = vivo;
            
            if((vizinhanca >= 4) && status == vivo)
            statusNovo = morto;
        }
        public void atualiza()
        {
            status = statusNovo;
        }
    }   
    public void init()
    {
        int i,j;
        for(i = 0; i < profundidade; i++)
        {
            for(j=0; j < largura; j++)
            {
                campo[i][j] = new Celula(i,j);
            }
         }
     }
     
    public boolean posicaoValida(int i, int j)
    {
        if((i >= 0) && (j >=0) && (i < profundidade) && ( j < largura))
        {
        return true;
        }
        return false;
    }
    
    
    public void atualiza()
    {
        int i,j;
        for(i = 0; i < profundidade; i++)
        {
           for(j=0; j < largura; j++)
           {
               campo[i][j].proxEstado();
           }
        }
        for(i = 0; i < profundidade; i++)
        {
           for(j=0; j < largura; j++)
           {
               campo[i][j].atualiza();
           }
        }
    }
    
    public void setPontoVivo(int i, int j)
    {
        campo[i][j].setStatus(vivo);
    }
    
    public void definePtos(int n)
    {
        int i;
        Random gerador = new Random();
        for( i = 1; i <= n; i++)
        {
            setPontoVivo(gerador.nextInt(profundidade),gerador.nextInt(largura));
        }
    }
    
    public void geraQuadrado()
    {
        int i,j;
        for(i = 25; i < 65; i++)
        {
           for(j=25; j < 65; j++)
           {
               campo[i][j].setStatus(vivo);
           }
        }
    }
    public void limpa()
    {
        for(int linha = 0; linha < profundidade; linha++)
        {
            for(int col = 0; col < largura; col++)
            {
                campo[linha][col] = null;
            }
        }
    }
    public int getProfundidade()
    {
        return profundidade;
    }
    public Color getStatus(int linha,int coluna)
    {
        return campo[linha][coluna].getStatus();
    }
    public int getLargura()
    {
        return largura;
    }
}
