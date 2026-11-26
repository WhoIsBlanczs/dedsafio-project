package net.ruleta.client.ui;

import net.minecraft.client.gui.GuiGraphics;

public final class Iconos {
   public static final String[] CERRAR = new String[]{"#.....#", ".#...#.", "..#.#..", "...#...", "..#.#..", ".#...#.", "#.....#"};
   public static final String[] PAPELERA = new String[]{"..###..", "#######", ".#.#.#.", ".#.#.#.", ".#.#.#.", ".#.#.#.", ".#####."};
   public static final String[] LUPA = new String[]{".####...", "#....#..", "#....#..", "#....#..", "#....#..", ".####...", "....##..", ".....##."};
   public static final String[] CHECK = new String[]{"......#", ".....##", "#...##.", "##.##..", ".###...", "..#...."};
   public static final String[] FLECHA = new String[]{"#...", ".#..", "..#.", "...#", "..#.", ".#..", "#..."};
   public static final String[] REINICIAR = new String[]{"..###..", ".#...#.", "#.....#", "#....##", "#...#.#", ".#.....", "..###.."};
   public static final String[] IR = new String[]{"...#...", "..###..", ".#####.", "...#...", "...#...", "...#...", "...#..."};
   public static final String[] BUNKER = new String[]{"...##...", "..####..", ".######.", "########", "#..##..#", "#..##..#", "########"};
   public static final String[] ENGRANAJE = new String[]{"...##...", ".#.##.#.", "..####..", "###..###", "###..###", "..####..", ".#.##.#.", "...##..."};
   public static final String[] JUGADORES = new String[]{"..####..", "..####..", "..####..", "...##...", ".######.", "########", "########", "##....##"};
   public static final String[] RELOJ = new String[]{"..####..", ".#....#.", "#..#...#", "#..#...#", "#..###.#", "#......#", ".#....#.", "..####.."};
   public static final String[] ECLIPSE = new String[]{"..####..", ".######.", "###..###", "##....##", "##....##", "###..###", ".######.", "..####.."};
   public static final String[] ESTRELLA = new String[]{"....#....", "....#....", "...###...", "#########", ".#######.", "..#####..", ".###.###.", ".##...##."};
   public static final String[] PANTALLA = new String[]{"#########", "#.......#", "#.#####.#", "#.#...#.#", "#.#####.#", "#.......#", "#########", "...###..."};

   private Iconos() {
   }

   public static int ancho(String[] icono) {
      return icono[0].length();
   }

   public static int alto(String[] icono) {
      return icono.length;
   }

   public static void dibujar(GuiGraphics g, String[] icono, int x, int y, int rgb, float alfa) {
      int col = Ui.c(rgb, alfa);
      if (col >>> 24 != 0) {
         for (int j = 0; j < icono.length; j++) {
            String fila = icono[j];
            int inicio = -1;

            for (int i = 0; i <= fila.length(); i++) {
               boolean lleno = i < fila.length() && fila.charAt(i) == '#';
               if (lleno && inicio < 0) {
                  inicio = i;
               } else if (!lleno && inicio >= 0) {
                  g.fill(x + inicio, y + j, x + i, y + j + 1, col);
                  inicio = -1;
               }
            }
         }
      }
   }
}
