import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Toolkit;

public class VisãoDoSimulador extends JFrame
{

    private static final Color COR_VAZIA = Color.white;
    private static final Color COR_DESCONHECIDA = Color.gray;
    private VisãoDoCampo visãoDoCampo;
    private Map<Class, Color> cores;
    private Campo campo;
    public JButton botao1,botao2,botao3;
    public Vida vida;
    
    public VisãoDoSimulador(int altura, int largura)
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cores = new LinkedHashMap<Class, Color>();
        campo = new Campo(altura,largura);
        setTitle("UNICENTRO Conway's Simulation");
        setLocation(100, 50);
        botao1 = new JButton("Gera Aleatórios");
        botao2 = new JButton("Inicia/Para Simulação");
        botao3 = new JButton("Gera quadrado");
        visãoDoCampo = new VisãoDoCampo(altura, largura);
        Container conteúdo = getContentPane();
        conteúdo.add(visãoDoCampo, BorderLayout.CENTER);
        vida = new Vida();
        
        /*
         * Botoes do capeta
         */
        JPanel painel = new JPanel();
        painel.add(botao1,BorderLayout.WEST);
        painel.add(botao2,BorderLayout.CENTER);
        painel.add(botao3,BorderLayout.EAST);
        conteúdo.add(painel,BorderLayout.SOUTH);
        botao1.addActionListener(new actionBotao1());
        botao2.addActionListener(new actionBotao2());
        botao3.addActionListener(new actionBotao3());
        
        pack();
        setVisible(true);
        mostraSituação();
    }

    private class actionBotao1 implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String resposta = JOptionPane.showInputDialog("Quantas células aleatórias?");
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
            mostraSituação();
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
                mostraSituação();
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
    
    public void mostraSituação()
    {
        if(!isVisible())
            setVisible(true);
        visãoDoCampo.preparaPintura();
        for(int linha = 0; linha < campo.getProfundidade(); linha++) 
        {
            for(int col = 0; col < campo.getLargura(); col++) 
            {
                Color cor = campo.getStatus(linha, col);
                visãoDoCampo.desenhaMarca(col, linha, cor);
             }
           }
        visãoDoCampo.repaint();
    }
    public void geraAleatorios(int quantidade)
    {
        campo.definePtos(quantidade);
        mostraSituação();
    }
    public void atualizaCampo()
    {
        campo.atualiza();
        mostraSituação();
    }

    private class VisãoDoCampo extends JPanel
    {
        private final int FATOR_DE_ESCALA_DA_VISÃO_DA_GRADE = 6;

        private int larguraGrade, alturaGrade;
        private int escalaX, escalaY;
        Dimension tamanho;
        private Graphics g;
        private Image imagemDoCampo;

        /**
         * Cria um novo componente VisãoDoCampo.
         */
        public VisãoDoCampo(int altura, int largura)
        {
            alturaGrade = altura;
            larguraGrade = largura;
            tamanho = new Dimension(0, 0);
        }

        /**
         * Informa ao gerente da IGU quão grande nós gostaríamos de ser.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(larguraGrade * FATOR_DE_ESCALA_DA_VISÃO_DA_GRADE,
                                 alturaGrade * FATOR_DE_ESCALA_DA_VISÃO_DA_GRADE);
        }

        /**
         * Prepara para um novo round de pintura. Como o componente
         * pode ser redimensionado, calcula o fato de escala novamente.
         */
        public void preparaPintura()
        {
            if(! tamanho.equals(getSize())) {  // se o tamanho se modificou...
                tamanho = getSize();
                imagemDoCampo = visãoDoCampo.createImage(tamanho.width, tamanho.height);
                g = imagemDoCampo.getGraphics();

                escalaX = tamanho.width / larguraGrade;
                if(escalaX < 1) {
                    escalaX = FATOR_DE_ESCALA_DA_VISÃO_DA_GRADE;
                }
                escalaY = tamanho.height / alturaGrade;
                if(escalaY < 1) {
                    escalaY = FATOR_DE_ESCALA_DA_VISÃO_DA_GRADE;
                }
            }
        }
        
        /**
         * Pinta na locação da grade neste campo em uma cor dada.
         */
        public void desenhaMarca(int x, int y, Color cor)
        {
            g.setColor(cor);
            g.fillRect(x * escalaX, y * escalaY, escalaX-1, escalaY-1);
        }
        
        /**
         * O componente visão de campo precisa ser reexibido. 
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
