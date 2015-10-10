import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Toolkit;

public class Vis�oDoSimulador extends JFrame
{

    private static final Color COR_VAZIA = Color.white;
    private static final Color COR_DESCONHECIDA = Color.gray;
    private Vis�oDoCampo vis�oDoCampo;
    private Map<Class, Color> cores;
    private Campo campo;
    public JButton botao1,botao2,botao3;
    public Vida vida;
    
    public Vis�oDoSimulador(int altura, int largura)
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cores = new LinkedHashMap<Class, Color>();
        campo = new Campo(altura,largura);
        setTitle("UNICENTRO Conway's Simulation");
        setLocation(100, 50);
        botao1 = new JButton("Gera Aleat�rios");
        botao2 = new JButton("Inicia/Para Simula��o");
        botao3 = new JButton("Gera quadrado");
        vis�oDoCampo = new Vis�oDoCampo(altura, largura);
        Container conte�do = getContentPane();
        conte�do.add(vis�oDoCampo, BorderLayout.CENTER);
        vida = new Vida();
        
        /*
         * Botoes do capeta
         */
        JPanel painel = new JPanel();
        painel.add(botao1,BorderLayout.WEST);
        painel.add(botao2,BorderLayout.CENTER);
        painel.add(botao3,BorderLayout.EAST);
        conte�do.add(painel,BorderLayout.SOUTH);
        botao1.addActionListener(new actionBotao1());
        botao2.addActionListener(new actionBotao2());
        botao3.addActionListener(new actionBotao3());
        
        pack();
        setVisible(true);
        mostraSitua��o();
    }

    private class actionBotao1 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String resposta = JOptionPane.showInputDialog("Quantas c�lulas aleat�rias?");
            int quantidade = Integer.parseInt( resposta ); 
            geraAleatorios(quantidade);
        }
    }
    private class actionBotao2 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if( vida.isAlive()  )
            {
                vida.stop();
            }
            else
            {
                vida = new Vida();
                vida.start();
            }
        }
    }
    private class actionBotao3 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            campo.geraQuadrado();
            mostraSitua��o();
        }
    }

    private Color getCor(Class classeDeAnimal)
    {
        Color col = cores.get(classeDeAnimal);
        if(col == null) {
            // nenhum cor definida para esta classe
            return COR_DESCONHECIDA;
        }
        else {
            return col;
        }
    }
    
    private class Vida extends Thread
    {
        public Vida()
        {
        }
        public void run()
        {
            int i,j;
            while( !vazio() )
            {
                for(i = 0; i < campo.getProfundidade(); i++)
                {
                    for(j=0; j < campo.getLargura(); j++)
                    {
                        campo.campo[i][j].proxEstado();
                    }
                }
                for(i = 0; i < campo.getProfundidade(); i++)
                {
                    for(j=0; j < campo.getLargura(); j++)
                    {
                        campo.campo[i][j].atualiza();
                    }
                }
                try 
                {    
                    Thread.sleep(100); 
                }
                catch (Exception e) 
                {     
                    System.out.println("Erro em Tread.sleep()");
                }
                mostraSitua��o();
            }
        }
    }
    
    public boolean vazio()
    {
        int i,j;
        for(i = 0; i < campo.getProfundidade(); i++)
        {
            for(j=0; j < campo.getLargura(); j++)
            {   
                if( campo.campo[i][j].getStatus() == campo.vivo)
                return false;
            }
        }
        return true;
    }
    
    public void mostraSitua��o()
    {
        if(!isVisible())
            setVisible(true);
        vis�oDoCampo.preparaPintura();
        for(int linha = 0; linha < campo.getProfundidade(); linha++) 
        {
            for(int col = 0; col < campo.getLargura(); col++) 
            {
                Color cor = campo.getStatus(linha, col);
                vis�oDoCampo.desenhaMarca(col, linha, cor);
             }
           }
        vis�oDoCampo.repaint();
    }
    public void geraAleatorios(int quantidade)
    {
        campo.definePtos(quantidade);
        mostraSitua��o();
    }
    public void atualizaCampo()
    {
        campo.atualiza();
        mostraSitua��o();
    }

    private class Vis�oDoCampo extends JPanel
    {
        private final int FATOR_DE_ESCALA_DA_VIS�O_DA_GRADE = 6;

        private int larguraGrade, alturaGrade;
        private int escalaX, escalaY;
        Dimension tamanho;
        private Graphics g;
        private Image imagemDoCampo;

        /**
         * Cria um novo componente Vis�oDoCampo.
         */
        public Vis�oDoCampo(int altura, int largura)
        {
            alturaGrade = altura;
            larguraGrade = largura;
            tamanho = new Dimension(0, 0);
        }

        /**
         * Informa ao gerente da IGU qu�o grande n�s gostar�amos de ser.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(larguraGrade * FATOR_DE_ESCALA_DA_VIS�O_DA_GRADE,
                                 alturaGrade * FATOR_DE_ESCALA_DA_VIS�O_DA_GRADE);
        }

        /**
         * Prepara para um novo round de pintura. Como o componente
         * pode ser redimensionado, calcula o fato de escala novamente.
         */
        public void preparaPintura()
        {
            if(! tamanho.equals(getSize())) {  // se o tamanho se modificou...
                tamanho = getSize();
                imagemDoCampo = vis�oDoCampo.createImage(tamanho.width, tamanho.height);
                g = imagemDoCampo.getGraphics();

                escalaX = tamanho.width / larguraGrade;
                if(escalaX < 1) {
                    escalaX = FATOR_DE_ESCALA_DA_VIS�O_DA_GRADE;
                }
                escalaY = tamanho.height / alturaGrade;
                if(escalaY < 1) {
                    escalaY = FATOR_DE_ESCALA_DA_VIS�O_DA_GRADE;
                }
            }
        }
        
        /**
         * Pinta na loca��o da grade neste campo em uma cor dada.
         */
        public void desenhaMarca(int x, int y, Color cor)
        {
            g.setColor(cor);
            g.fillRect(x * escalaX, y * escalaY, escalaX-1, escalaY-1);
        }
        
        /**
         * O componente vis�o de campo precisa ser reexibido. 
         * Copia a imagem interna para a tela.
         */
        public void paintComponent(Graphics g)
        {
            if(imagemDoCampo != null) {
                Dimension tamanhoAtual = getSize();
                if(tamanho.equals(tamanhoAtual)) {
                    g.drawImage(imagemDoCampo, 0, 0, null);
                }
                else {
                    // Reescala a imagem anterior.
                    g.drawImage(imagemDoCampo, 0, 0, tamanhoAtual.width, tamanhoAtual.height, null);
                }
            }
        }
    }
}
