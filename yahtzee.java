import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/*************************************************************************/
//  yahtzee
//  programmed by Yakkun
//  yahtzee�ƌĂ΂��T�C�R�����g���ÓT�I�ȃQ�[���ł�
/*************************************************************************/
public class yahtzee extends Applet implements KeyListener, Runnable {
    //�O���[�o���ϐ��̐錾�����܂�
    /*�萔*/
    //�����҂�����(�~���b)
    final int MSEC_WAIT = 50;
    //�^�C�g��
    final int GAME_TITLE = 0;
    //�Q�[����
    final int GAME_OPEN = 1;
    //�Q�[���I��
    final int GAME_END = 2;
    //�X�R�A(1)
    final int SCR_1 = 0;
    //�X�R�A(2)
    final int SCR_2 = 1;
    //�X�R�A(3)
    final int SCR_3 = 2;
    //�X�R�A(4)
    final int SCR_4 = 3;
    //�X�R�A(5)
    final int SCR_5 = 4;
    //�X�R�A(6)
    final int SCR_6 = 5;
    //�X�R�A(3card)
    final int SCR_3CARD = 6;
    //�X�R�A(4card)
    final int SCR_4CARD = 7;
    //�X�R�A(Full)
    final int SCR_FULL = 8;
    //�X�R�A(SStght)
    final int SCR_SSTGHT = 9;
    //�X�R�A(LStght)
    final int SCR_LSTGHT = 10;
    //�X�R�A(Chance)
    final int SCR_CHANCE = 11;
    //�X�R�A(Yahtzee)
    final int SCR_YAHTZEE = 12;
    //�y�A�`�F�b�N(3card)
    final int PCK_3CARD = 1;
    //�y�A�`�F�b�N(4card)
    final int PCK_4CARD = 2;
    //�y�A�`�F�b�N(Full)
    final int PCK_FULL = 3;
    //�y�A�`�F�b�N(Yahtzee)
    final int PCK_YAHTZEE = 4;
    
    /*�I�u�W�F�N�g�ϐ�*/
    //�T�C�R���C���[�W�Q�Ɨp
    Image card[] = null;
    //����
    Random rnd = null;
    //�X���b�h
    Thread thIyahtzee = null;
    //�X�R�A�\���p�z��
    String scoredisp[] = {"1",
                          "2",
                          "3",
                          "4",
                          "5",
                          "6",
                          "3card",
                          "4card",
                          "Full",
                          "SStght",
                          "LStght",
                          "Chance",
                          "Yahtzee"};
    
    // �_�u���o�b�t�@�����O�p
  Graphics offGraphics = null;
  Image offImage = null;
    
    /*�����^�ϐ�*/
    //�Q�[���X�e�[�^�X(������Ԃ̓^�C�g��)
    int gameStatus = GAME_TITLE;
    //�T�C�R�������\��
    int chance = 3;
    //�T�C�R���l
    int cardnum[];
    //���ݑI�����ꂽ�X�R�A
    int currentscorechkd = 0;
    //�X�R�A1
    int selectedscr1 = 0;
    //�X�R�A2
    int selectedscr2 = 0;
    //�X�R�A�I���\��
    int gameleft = 13;
    //�{�[�i�X�X�R�A�p
    int bonuscount = 0;
    //���v�_
    int total = 0;
    //���W�p
    int X = 5;
    //���W�p
    int X2 = 5;
    //���W�p
    int Y2 = 20;
    
    /*�t���O�ϐ�*/
    //�������t���O
    boolean goflush = false;
    //�����\�t���O
    boolean flushout = false;
    //�J�[�h�z�[���h�t���O
    boolean hold[];
    //�g�p�ς݃X�R�A�t���O
    boolean scoreused[];
    //�����������t���O
    boolean initflag = false;
    //�{�[�i�X�m�F�t���O
    boolean bonusflag = false;
    
    /*************************************************************************/
    //  �ϐ��̏������E�摜�f�[�^�̎擾���s��
    /*************************************************************************/
    public void init() {
      //�ϐ��̏�����
      hold = new boolean[5];
      scoreused = new boolean[13];
      card = new Image[6];
      cardnum = new int[5];
    
      //�T�C�R���̊G�𓾂� �C���[�W�I�u�W�F�N�g�z���gif�t�@�C�����i�[
      card[0] = getImage(getDocumentBase(), "ace.gif");
      card[1] = getImage(getDocumentBase(), "two.gif");
      card[2] = getImage(getDocumentBase(), "three.gif");
      card[3] = getImage(getDocumentBase(), "four.gif");
      card[4] = getImage(getDocumentBase(), "five.gif");
      card[5] = getImage(getDocumentBase(), "six.gif");
    
      //�����I�u�W�F�N�g��������
      rnd = new Random();
    
      for (int i=0;i<5;i++){
        //�����\���̃T�C�R���ԍ��𓾂�
        cardnum[i] = Math.abs(rnd.nextInt()) % 6;
      }

      //�L�[���X�i�[�o�^
      addKeyListener(this);

    // Double buffer image and graphics (larger display)
    offImage = createImage(300, 320);
    offGraphics = offImage.getGraphics();
    
      //�X���b�h�̏������E�J�n ������Ԃ̃X�R�A���v�Z����
      thIyahtzee=new Thread(this);
      thIyahtzee.start();
      chkscore1();
      chkscore2();
    }
    
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}

    /*************************************************************************/
    //  �C�x���g���擾����֐�
    /*************************************************************************/
    public void keyPressed(KeyEvent e) {
        //�Q�[���X�e�[�^�X
        switch (gameStatus) {
          //�^�C�g���̏ꍇ
          case GAME_TITLE:
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              gameStatus = GAME_OPEN;
            }
            break;
          //�Q�[���I��
          case GAME_END:
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              //�ϐ��̏�����
              for (int i1 = 0; i1 < 13; i1++) {
                scoreused[i1] = false;
              }
              gameStatus = GAME_OPEN;
              total = 0;
              selectedscr1 = 0;
              selectedscr2 = 0;
              chkscore1();
              chkscore2();
            }
            break;
          //�Q�[����
          case GAME_OPEN:
            //�ԍ��L�[�̏���
            //�T�C�R���̃z�[���h
            if (e.getKeyCode() == KeyEvent.VK_1 && !goflush) {
              hold[0] = rtnhldcond(hold[0]);
            }
            if (e.getKeyCode() == KeyEvent.VK_2 && !goflush) {
              hold[1] = rtnhldcond(hold[1]);
            }
            if (e.getKeyCode() == KeyEvent.VK_3 && !goflush) {
              hold[2] = rtnhldcond(hold[2]);
            }
            if (e.getKeyCode() == KeyEvent.VK_4 && !goflush) {
              hold[3] = rtnhldcond(hold[3]);
            }
            if (e.getKeyCode() == KeyEvent.VK_5 && !goflush) {
              hold[4] = rtnhldcond(hold[4]);
            }
            //�Z���N�g�L�[�̏���
            //�T�C�R�����܂킷
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
              if (chance > 0 && !flushout) {
                if (!goflush) {
                  goflush = true;
                  if (!initflag) {
                    chance--;
                  } else {
                    initflag = false;
                  }
                } else if (goflush) {
                  goflush = false;
                  chkscore1();
                  chkscore2();
                }
              } else if (goflush) {
                goflush = false;
                chkscore1();
                chkscore2();
              } else {
                flushout = true;
              }
            }
            //���L�[�̏���
            //�X�R�A�̑I��
            if (e.getKeyCode() == KeyEvent.VK_UP) {
              currentscorechkd--;
              if (currentscorechkd < SCR_1) {
                currentscorechkd = SCR_YAHTZEE;
              }
    
              while(scoreused[currentscorechkd]) {
                currentscorechkd--;
                if (currentscorechkd < SCR_1) {
                  currentscorechkd = SCR_YAHTZEE;
                }
              }
              chkscore1();
              chkscore2();
            }
            //���L�[�̏���
            //�X�R�A�̑I��
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
              currentscorechkd++;
              if (currentscorechkd > SCR_YAHTZEE) {
                currentscorechkd = SCR_1;
              }
              while(scoreused[currentscorechkd]) {
                currentscorechkd++;
                if (currentscorechkd > SCR_YAHTZEE) {
                  currentscorechkd = SCR_1;
                }
              }
              chkscore1();
              chkscore2();
            }
            //�\�t�g�L�[1�̏���
            //�X�R�A�̓���
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              enterscr();
            }
            break;
          default:
            System.out.println("processEvent():ERROR UNEXPECTED VALUE:gameStatus = " + gameStatus);
            break;
        }
    
        //�Q�[���I���L�[(���ł�������)
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          destroy();
        }
    }
    
    /*************************************************************************/
    //  �z�[���h��Ԃ��`�F�b�N����֐�
    /*************************************************************************/
    public boolean rtnhldcond(boolean incnd) {
      if (!incnd) {
        return(true);
      } else {
        return(false);
      }
    }
    
    /*************************************************************************/
    //  �`�ʊ֐�
    /*************************************************************************/
    public void paint(Graphics g) {
      // Game state
      switch (gameStatus) {
        case GAME_TITLE:
          offGraphics.clearRect(0, 0, 300, 320);
          offGraphics.setColor(Color.GRAY);
          offGraphics.fillRect(0, 0, 300, 320);
          offGraphics.setColor(Color.YELLOW);
          offGraphics.drawString("iYahtzee", 80, 60);
          offGraphics.setColor(Color.RED);
          offGraphics.drawString("PRESS ENTER", 60, 120);
          offGraphics.setColor(Color.BLUE);
          offGraphics.drawString("(c)2003", 110, 180);
          offGraphics.drawString("Y&Y FACTORY", 90, 200);
          break;
        case GAME_END:
          offGraphics.clearRect(0, 0, 300, 320);
          offGraphics.setColor(Color.GRAY);
          offGraphics.fillRect(0, 0, 300, 320);
          offGraphics.setColor(Color.BLUE);
          offGraphics.drawString("GAME OVER", 80, 60);
          offGraphics.setColor(Color.YELLOW);
          offGraphics.drawString("YOUR SCORE IS " + total + ".", 30, 120);
          break;
        case GAME_OPEN:
          offGraphics.clearRect(0, 0, 300, 320);
          offGraphics.setColor(Color.GRAY);
          offGraphics.fillRect(0, 0, 300, 320);
          // Draw dice
          for (int c=0;c<5;c++) {
            offGraphics.drawImage(card[cardnum[c]], X, 220, this);
            X+=60;
            if (X > 260) {
              X = 5;
            }
          }
          // Draw score (upper)
          for (int c1=0;c1<6;c1++) {
            if (scoreused[c1]) {
              offGraphics.setColor(Color.WHITE);
            } else {
              offGraphics.setColor(Color.BLACK);
            }
            if (currentscorechkd == c1) {
              offGraphics.setColor(Color.RED);
            }
            offGraphics.drawString(scoredisp[c1], X2, Y2);
            Y2+=20;
          }
          Y2-=120;
          X2+=60;
          // Draw score (lower)
          for (int c2=6;c2<13;c2++) {
            if (scoreused[c2]) {
              offGraphics.setColor(Color.WHITE);
            } else {
              offGraphics.setColor(Color.BLACK);
            }
            if (currentscorechkd == c2) {
              offGraphics.setColor(Color.RED);
            }
            offGraphics.drawString(scoredisp[c2], X2, Y2);
            Y2+=20;
          }
          Y2 -= 140;
          X2 -= 60;
          // Draw total
          offGraphics.setColor(Color.BLUE);
          offGraphics.drawString("Total:" + total, 20, 30);
          if (bonusflag) {
            offGraphics.drawString("BONUS35", 180, 30);
          }
          if (goflush) {
            offGraphics.setColor(Color.YELLOW);
            offGraphics.drawString("Rolling...", 180, 60);
            offGraphics.drawString("PRESS STOP", 180, 90);
            offGraphics.setColor(Color.BLUE);
          }
          if (initflag) {
            offGraphics.setColor(Color.YELLOW);
            offGraphics.drawString("PRESS ROLL", 180, 90);
            offGraphics.setColor(Color.BLUE);
          } else {
            offGraphics.drawString("GAME START", 180, 120);
            offGraphics.drawString("Rolls:"+chance, 20, 270);
            offGraphics.drawString("Score:"+(selectedscr1+selectedscr2), 180, 270);
          }
          // Draw hold
          for (int i=0;i<5;i++) {
            if (hold[i]) {
              offGraphics.drawString("HLD", X, 300);
            }
            X+=60;
            if (X > 260) {
              X = 5;
            }
          }
          break;
        default:
          System.out.println("paint():ERROR UNEXPECTED VALUE:gameStatus = " + gameStatus);
          break;
      }
      // Draw to screen
    g.drawImage(offImage, 0, 0, this);
    }
    
  public void update(Graphics g)
  {
    paint(g);
  }
  
    /*************************************************************************/
    //  �X���b�h�֐�
    /*************************************************************************/
    public void run() {
      while(true) {
        try{
          //���������
          Thread.sleep(MSEC_WAIT);
        } catch (Throwable th) {
          System.out.println("run():SYSTEM ERROR: " + th.toString());
          break;
        }
    
        flushcards();
        repaint();
      }
    }
    
    /*************************************************************************/
    //  �T�C�R���̒l�𓾂邽�߂̗����擾�֐�(�T�u���[�`��)
    /*************************************************************************/
    public void flushcards() {
      if (goflush) {
        for (int i = 0; i < 5; i++) {
          if (!hold[i]) {
            cardnum[i] = Math.abs(rnd.nextInt()) % 6;
          }
        }
      }
    }
    
    /*************************************************************************/
    //  �X�R�A�`�F�b�N�֐�1(�T�u���[�`��)
    //  �T�C�R���̖ڕʂ̍��v�̃X�R�A�I�����̃X�R�A���v�Z����
    /*************************************************************************/
    public void chkscore1() {
      //1�̏o�ڑI����
      if (currentscorechkd == SCR_1) {
        selectedscr1 = chknums(1, SCR_1, cardnum);
      //2�̏o�ڑI����
      } else if (currentscorechkd == SCR_2) {
        selectedscr1 = chknums(2, SCR_2, cardnum);
      //3�̏o�ڑI����
      } else if (currentscorechkd == SCR_3) {
        selectedscr1 = chknums(3, SCR_3, cardnum);
      //4�̏o�ڑI����
      } else if (currentscorechkd == SCR_4) {
        selectedscr1 = chknums(4, SCR_4, cardnum);
      //5�̏o�ڑI����
      } else if (currentscorechkd == SCR_5) {
        selectedscr1 = chknums(5, SCR_5, cardnum);
      //6�̏o�ڑI����
      } else if (currentscorechkd == SCR_6) {
        selectedscr1 = chknums(6, SCR_6, cardnum);
      //���̏�Ԃ̎��̓y�A�n�E�X�g���[�g�n�̃X�R�A���`�F�b�N��
      } else {
        selectedscr1 = 0;
      }
    }
    
    /*************************************************************************/
    //  �o�ڌn�X�R�A�������݊֐�
    //  @param actscr �ڕʂ̓_��
    //  @param cdnum �T�C�R���̏o�ڎ��
    //  @param inscr[] ���͂��ꂽ�T�C�R���̏o��
    //  @return int �X�R�A�̕ԋp
    /*************************************************************************/
    public int chknums(int actscr,int cdnum,int inscr[]) {
      int scre=0;
      for (int i = 0; i < 5; i++) {
        //�����̖ڂ��������̂�����������
        if (inscr[i] == cdnum) {
          scre+=actscr;
        }
      }
      return(scre);
    }
    
    /*************************************************************************/
    //  �X�R�A�`�F�b�N�֐�2(�T�u���[�`��)
    //  �y�A�n�E�X�g���[�g�n�̃X�R�A���`�F�b�N����
    /*************************************************************************/
    public void chkscore2() {
      //�T�u�X�R�A�i�[�z��
      int subscr[] = new int[8];
      //�z�[���h�X�R�A
      int hldscr = 0;
      //�y�A�n�`�F�b�N�X�e�[�^�X
      int pairChkdStatus = 0;
      //�T�u�X�R�A�i�[�z��փR�s�[
      for (int c=0;c<5;c++) {
        subscr[c] = cardnum[c];
      }
      //�\�[�g���s��(�o�u���\�[�g)
       for (int i = 0; i < 4; i++) {
         for (int ii = 0; ii < 4; ii++) {
           int first = subscr[ii];
           int second = subscr[ii + 1];
    
           if (first > second) {
             subscr[ii] = second;
             subscr[ii+1] = first;
           }
         }
      }
      //�\�[�g�ς̃T�u�X�R�A�Ńy�A�n�`�F�b�N���s��
      pairChkdStatus = rtnchpair(subscr);
      //�X���[�J�[�h�I����
      if (currentscorechkd == SCR_3CARD) {
        //�S�ẴX�e�[�^�X�����݂���ꍇ�K���X���[�J�[�h�͐������Ă���
        if (pairChkdStatus == PCK_3CARD || pairChkdStatus == PCK_4CARD || pairChkdStatus == PCK_FULL || pairChkdStatus == PCK_YAHTZEE) {
          for (int i12 = 0; i12 < 5; i12++) {
            hldscr+=subscr[i12];
            hldscr++;
          }
          selectedscr2 = hldscr;
        } else {
          selectedscr2 = 0;
        }
      //�t�H�[�J�[�h�I����
      } else if (currentscorechkd == SCR_4CARD) {
        //�t�H�[�J�[�h��YAHTZEE�̃X�e�[�^�X�����݂���ꍇ
        if (pairChkdStatus == PCK_4CARD || pairChkdStatus == PCK_YAHTZEE) {
          for (int i22 = 0; i22 < 5; i22++) {
            hldscr+=subscr[i22];
            hldscr++;
          }
          selectedscr2 = hldscr;
        } else {
          selectedscr2 = 0;
        }
      //�t���n�E�X�I����
      } else if (currentscorechkd == SCR_FULL) {
        //�t���n�E�X�̃X�e�[�^�X�����݂���ꍇ
        if (pairChkdStatus == PCK_FULL) {
          selectedscr2 = 25;
        } else {
          selectedscr2=0;
        }
      //���X�g���[�g�I����
      } else if (currentscorechkd == SCR_SSTGHT) {
        int smstraightflag=0;
        for (int i4 = 0; i4 < 4; i4++) {
          if (subscr[i4] == (subscr[i4 + 1] - 1)) {
            smstraightflag++;
          }
        }
        if (smstraightflag > 2) {
          selectedscr2 = 30;
        } else {
          selectedscr2 = 0;
        }
      //��X�g���[�g�I����
      } else if (currentscorechkd == SCR_LSTGHT) {
        int lgstraightflag=0;
        for (int i5 = 0; i5 < 4; i5++) {
          if (subscr[i5] == (subscr[i5 + 1] - 1)) {
            lgstraightflag++;
          }
        }
    
        if (lgstraightflag == 4) {
          selectedscr2 = 40;
        } else {
          selectedscr2 = 0;
        }
      //�`�����X�I����
      } else if (currentscorechkd == SCR_CHANCE) {
        for (int i6 = 0; i6 < 5; i6++) {
          hldscr+=subscr[i6];
          hldscr++;
        }
        selectedscr2=hldscr;
      //YAHTZEE�I����
      } else if (currentscorechkd == SCR_YAHTZEE) {
        //YAHTZEE�̃X�e�[�^�X�����݂���ꍇ
        if (pairChkdStatus == PCK_YAHTZEE) {
          selectedscr2 = 50;
        } else {
          selectedscr2 = 0;
        }
      //���̏�Ԃ̎��̓T�C�R���̖ڕʂ̍��v�̃X�R�A���`�F�b�N��
      } else {
        selectedscr2 = 0;
      }
    }
    
    /*************************************************************************/
    //  �y�A�`�F�b�N�֐�
    //  @param incd[] ���̓J�[�h(�K���\�[�g�ςł��邱��)
    //  @return int �X�R�A���ʎq�ԋp
    /*************************************************************************/
    public int rtnchpair(int incd[]) {
      //�y�A�J�E���g
      int pair = 0;
      //�X���[�J�[�h�t���O
      boolean threecardflag = false;
      //�t�H�[�J�[�h�t���O
      boolean fourcardflag = false;
      //YAHTZEE�t���O
      boolean yahtflag = false;
      //�擪4���̃��[�v
      for (int i = 0; i < 4; i++) {
        if (incd[i] == incd[i + 1]) {
          //�y�A�J�E���g�A�b�v
          pair++;
          //�y�A��1�L�肩��2��ɓ����J�[�h������ꍇ
          if ((pair == 1) && (incd[i] == incd[i + 2])) {
            //�X���[�J�[�h�L�茈��
            threecardflag = true;
            //�X���[�J�[�h���L�肩��3��ɓ����J�[�h������ꍇ
            if (threecardflag && (incd[i] == incd[i + 3])) {
              //�t�H�[�J�[�h�L�茈��
              fourcardflag = true;
              //�t�H�[�J�[�h�����肩��4��ɓ����J�[�h������ꍇ
              if (fourcardflag && (incd[i] == incd[i + 4])) {
                //YAHTZEE�L�茈��
                yahtflag = true;
              }
            }
          }
        }
      }
      //YAHTZEE�L�莞
      if (yahtflag) {
        return(PCK_YAHTZEE);
      }
      //�t�H�[�J�[�h�L�莞
      if (fourcardflag) {
        return(PCK_4CARD);
      //�t�H�[�J�[�h�L��łȂ����y�A�J�E���g��3�̂Ƃ�
      //(�X���[�J�[�h�͂��邩������Ȃ����y�A�J�E���g��3���鎞��
      //�t�H�[�J�[�h���t���n�E�X�������肦�Ȃ�)
      //�t���n�E�X�̏ꍇ�͕K���X���[�J�[�h����������̂�
      //���̂悤�ɐ��䂵�Ă���
      } else if (!fourcardflag && pair == 3) {
        return(PCK_FULL);
      //�X���[�J�[�h�L�莞
      } else if (threecardflag) {
        return(PCK_3CARD);
      //�������Ă͂܂�Ȃ���
      } else {
        return(0);
      }
    }
    
    /*************************************************************************/
    //  �X�R�A���͊֐�(�T�u���[�`��)
    /*************************************************************************/
    public void enterscr() {
      //���ݑI������Ă���X�R�A���g�p����Ă��Ȃ��������������t���O���^�łȂ��ꍇ
      if (!scoreused[currentscorechkd] && !initflag) {
        total+=selectedscr1;
        total+=selectedscr2;
        bonuscount+=selectedscr1;
        //�{�[�i�X�̃`�F�b�N
        if (bonuscount > 62 && !bonusflag) {
          total = total + 35;
          bonusflag = true;
        }
    
        gameleft--;
        scoreused[currentscorechkd] = true;
        flushout = false;
        initflag = true;
        chance = 3;
        currentscorechkd = SCR_1;
        //�g�p����Ă��Ȃ��X�R�A��I������܂Ń��[�v
        while (scoreused[currentscorechkd] && currentscorechkd < SCR_YAHTZEE) {
          currentscorechkd++;
        }
        //�����̃z�[���h�̉���
        for (int i = 0; i < 5;i++) {
          hold[i] = false;
        }
      }
      //�c��Q�[����0�̏ꍇ�Q�[�����I��������
      if (gameleft == 0) {
        bonusflag = false;
        gameStatus = GAME_END;
        gameleft = 13;
      }
    }
}