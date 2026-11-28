package com.ghostsafio.mitomod;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class CuboPhoraShape {
   public static final int KEEP = -1;
   public static final int AIR = 0;
   public static final int PANEL = 1;
   public static final int TILE = 2;
   public static final int ORANGE = 3;
   public static final int CIRC_ORANGE = 4;
   public static final int YELLOW = 5;
   public static final int TEAL = 6;
   public static final int CIRC_TEAL = 7;
   public static final int RED = 8;
   public static final int LADDER_E = 9;
   public static final int LADDER_W = 10;
   public static final int LADDER_S = 11;
   public static final int LADDER_N = 12;
   public static final int DEEPSLATE = 13;
   public static final int COBBLED = 14;
   public static final int TUFF = 15;
   public static final int ANDESITE = 16;
   public static final int IMPULSOR = 17;
   public static final int COBBLE = 18;
   public static final int MOSSY = 19;
   public static final int PLANKS = 20;
   public static final int CHAIN = 21;
   public static final int VERDE = 22;
   public static final int COUNT = 23;
   public static final int MARGIN = 12;
   public static final int T = 5;
   public static final int DOOR_Y = 12;
   public static final int DOOR_HALF = 4;
   public static final int GROUND_REL = 6;
   public static final int PITCH = 18;
   public static final int CLEAR = 16;
   public final int S;
   public final int C;
   private final long seed;
   private final boolean[][][] lines;
   private final int H;
   private final int n;
   private final int R;
   private final int RC;
   private final int G;
   private final int stride;
   private final int i0;
   private final int j0;
   private static final int ROOM_H = 10;
   private static final int CORR_H = 7;
   private final int K;
   private final Map<Integer, CuboPhoraShape.Layout> mazes = new HashMap<>();

   public CuboPhoraShape(int size, long seed) {
      this.S = size | 1;
      this.C = this.S / 2;
      this.seed = seed;
      this.H = (this.S - 1 - 10) / 2;
      int w = this.H - 5;
      this.G = 8;
      this.n = Math.max(2, Math.round(w / 21.0F));
      this.R = (w - (this.n - 1) * this.G) / this.n;
      this.RC = this.R / 2;
      this.stride = this.R + this.G;
      this.i0 = this.n / 2;
      this.j0 = this.n / 2;
      this.K = Math.max(2, Math.min(4, (this.S - 1 - 5 - 10 - 11) / 18));
      this.lines = new boolean[5][this.S][this.S];

      for (int face = 0; face < 5; face++) {
         this.genLines(face);
      }
   }

   private static int hash(long a, long b, long c, long seed) {
      long h = a * -7046029254386353131L ^ b * -4417276706812531889L ^ c * 1609587929392839161L ^ seed;
      h ^= h >>> 32;
      h *= -2960836687051489901L;
      h ^= h >>> 32;
      return (int)(h & 2147483647L);
   }

   private void genLines(int face) {
      Random r = new Random(this.seed * 31L + face * 7919L + 13L);
      int walks = face == 4 ? 9 : 7;

      for (int w = 0; w < walks; w++) {
         int u = r.nextInt(this.S);
         int v = r.nextInt(this.S);
         int dir = r.nextInt(4);
         int[] du = new int[]{1, 0, -1, 0};
         int[] dv = new int[]{0, 1, 0, -1};
         int thick = r.nextInt(100) < 15 ? 2 : 1;

         for (int seg = 0; seg < 9; seg++) {
            int len = 5 + r.nextInt(11);
            boolean diag = r.nextInt(100) < 45;
            int ddu = du[dir];
            int ddv = dv[dir];
            int pu = du[(dir + 1) % 4];
            int pv = dv[(dir + 1) % 4];

            for (int i = 0; i < len; i++) {
               this.mark(face, u, v, thick);
               if (diag) {
                  u += ddu;
                  v += ddv;
                  this.mark(face, u, v, thick);
                  u += pu;
                  v += pv;
               } else {
                  u += ddu;
                  v += ddv;
               }
            }

            dir = (dir + (r.nextBoolean() ? 1 : 3)) % 4;
            if (u < -4 || v < -4 || u > this.S + 4 || v > this.S + 4) {
               break;
            }
         }
      }
   }

   private void mark(int face, int u, int v, int thick) {
      for (int i = 0; i < thick; i++) {
         for (int j = 0; j < thick; j++) {
            int a = u + i;
            int b = v + j;
            if (a >= 0 && a < this.S && b >= 0 && b < this.S) {
               this.lines[face][a][b] = true;
            }
         }
      }
   }

   private boolean inBox(int x, int y, int z) {
      return x >= 5 && x <= this.S - 1 - 5 && z >= 5 && z <= this.S - 1 - 5 && y >= 12 && y <= this.S - 1 - 5;
   }

   public int code(int x, int y, int z) {
      if (x < 0 || x >= this.S || z < 0 || z >= this.S) {
         return this.outside(x, y, z);
      }

      if (y >= 0 && y < this.S) {
         int door = this.doorCode(x, y, z);
         if (door != -2) {
            return door;
         } else {
            return this.inBox(x, y, z) ? this.interior(x, y, z) : this.shell(x, y, z);
         }
      } else {
         return -1;
      }
   }

   private int outside(int x, int y, int z) {
      boolean ox = x < 0 || x >= this.S;
      boolean oz = z < 0 || z >= this.S;
      if (ox && oz) {
         return -1;
      }

      int e;
      int u;
      if (ox) {
         e = x >= this.S ? x - this.S : -1 - x;
         u = z;
      } else {
         e = z >= this.S ? z - this.S : -1 - z;
         u = x;
      }

      if (y >= 0 && y < this.S) {
         int ramp = 12;
         if (e >= ramp) {
            return -1;
         } else {
            int hw = 6 + e / 2;
            if (Math.abs(u - this.C) > hw) {
               return -1;
            } else {
               int ht = 11 - e / 2;
               if (y <= ht) {
                  return this.rock(x, y, z);
               } else {
                  return y <= ht + 12 ? 0 : -1;
               }
            }
         }
      } else {
         return -1;
      }
   }

   private int rock(int x, int y, int z) {
      int r = hash(x, y, z, this.seed + 99L) % 100;
      if (r < 40) {
         return 13;
      } else if (r < 75) {
         return 14;
      } else {
         return r < 90 ? 15 : 16;
      }
   }

   private int doorCode(int x, int y, int z) {
      int df;
      int u;
      if (x <= 4) {
         df = x;
         u = z;
      } else if (x >= this.S - 5) {
         df = this.S - 1 - x;
         u = z;
      } else if (z <= 4) {
         df = z;
         u = x;
      } else {
         if (z < this.S - 5) {
            return -2;
         }

         df = this.S - 1 - z;
         u = x;
      }

      int lat = Math.abs(u - this.C);
      if (lat > 6) {
         return -2;
      }

      if (y >= 11 && y <= 23) {
         boolean opening = lat <= 4 && y >= 12 && y <= 21;
         if (opening) {
            return 0;
         } else if (y == 11 && lat <= 4) {
            return 5;
         } else if (df <= 2) {
            return 5;
         } else {
            return df == 4 ? 6 : 2;
         }
      } else {
         return -2;
      }
   }

   private int shell(int x, int y, int z) {
      int dE = this.S - 1 - x;
      int dW = x;
      int dS = this.S - 1 - z;
      int dN = z;
      int dT = this.S - 1 - y;
      int face = 0;
      int d = dE;
      if (dW < d) {
         d = dW;
         face = 1;
      }

      if (dS < d) {
         d = dS;
         face = 2;
      }

      if (dN < d) {
         d = dN;
         face = 3;
      }

      if (dT < d) {
         d = dT;
         face = 4;
      }

      int skin = this.skinFace(x, y, z);
      if (skin != -1) {
         return this.innerSkin(skin, x, y, z);
      }

      if (d < 3 && y > 4) {
         int u;
         int v;
         if (face <= 1) {
            u = z;
            v = y;
         } else if (face <= 3) {
            u = x;
            v = y;
         } else {
            u = x;
            v = z;
         }

         boolean near = face < 4 && Math.abs(u - this.C) <= 10 && v >= 9 && v <= 26;
         if (!near && this.lines[face][u][v]) {
            return d == 2 ? 3 : 0;
         }

         int p = 0;
         if (!near) {
            int r = hash(face, u >> 2, v >> 2, this.seed) % 100;
            p = r < 55 ? 0 : (r < 80 ? 1 : 2);
         }

         if (d < 2 - p) {
            return 0;
         } else if (d == 2 - p) {
            int r = hash(x, y, z, this.seed + 5L) % 100;
            return r < 6 ? 4 : 1;
         } else {
            return 1;
         }
      } else {
         return 1;
      }
   }

   private int skinFace(int x, int y, int z) {
      if (this.inBox(x + 1, y, z)) {
         return 1;
      } else if (this.inBox(x - 1, y, z)) {
         return 0;
      } else if (this.inBox(x, y, z + 1)) {
         return 3;
      } else if (this.inBox(x, y, z - 1)) {
         return 2;
      } else if (this.inBox(x, y + 1, z)) {
         return 5;
      } else {
         return this.inBox(x, y - 1, z) ? 6 : -1;
      }
   }

   private static boolean gridLine(int u, int v) {
      int a = Math.floorMod(u, 24);
      int b = Math.floorMod(v, 24);
      if (a != 12 && b != 12) {
         boolean in = a >= 3 && a <= 21 && b >= 3 && b <= 21;
         return in && (a == 3 || a == 21 || b == 3 || b == 21);
      } else {
         return true;
      }
   }

   private int innerSkin(int face, int x, int y, int z) {
      int u;
      int v;
      if (face <= 1) {
         u = z;
         v = y;
      } else if (face <= 3) {
         u = x;
         v = y;
      } else {
         u = x;
         v = z;
      }

      boolean red = hash(face, u / 40, v / 40, this.seed + 3L) % 100 < 22;
      if (gridLine(u, v)) {
         return red ? 8 : 6;
      } else {
         int r = hash(x, y, z, this.seed + 8L) % 100;
         if (face == 5) {
            return r < 10 ? 7 : 2;
         } else {
            return r < 12 ? 7 : (r < 22 ? 2 : 1);
         }
      }
   }

   public int roomCount() {
      return 4 * this.K * this.n * this.n;
   }

   private int interior(int x, int y, int z) {
      int ix = x - this.C;
      int iz = z - this.C;
      int ax = Math.abs(ix);
      int az = Math.abs(iz);
      if (ax > 2 || az > 2) {
         int u0 = y - 12;
         int k = u0 / 18;
         int r = u0 % 18;
         if (k >= this.K) {
            return 0;
         } else {
            return r >= 16 ? this.slab(k, x, y, z, ax, az, r) : this.roomLayer(k, r, x, y, z, ix, iz, ax, az);
         }
      } else if (ax != 2 && az != 2) {
         return 1;
      } else {
         int u = ax == 2 ? z : x;
         if (gridLine(u, y)) {
            return 8;
         } else {
            return hash(x, y, z, this.seed + 21L) % 100 < 14 ? 4 : 1;
         }
      }
   }

   private int slab(int k, int x, int y, int z, int ax, int az, int r) {
      if (this.padHole(k, ax, az)) {
         return 0;
      }

      boolean top = r == 17;
      int a = Math.floorMod(x, 24);
      int b = Math.floorMod(z, 24);
      if (top) {
         boolean red = hash(x / 40, y, z / 40, this.seed + 4L) % 100 < 22;
         if (a != 12 && b != 12) {
            return hash(x, y, z, this.seed + 9L) % 100 < 8 ? 7 : 2;
         } else {
            return red ? 8 : 6;
         }
      } else if (gridLine(x, z)) {
         return 6;
      } else {
         return hash(x, y, z, this.seed + 11L) % 100 < 10 ? 7 : 1;
      }
   }

   private int roomColor(int roomId) {
      int c = hash(roomId, 5L, 5L, this.seed + 77L) % 100;
      return c < 55 ? 6 : (c < 80 ? 8 : 3);
   }

   private int wall(int u, int v, int x, int y, int z, int color) {
      if (gridLine(u, v)) {
         return color;
      }

      int r = hash(x, y, z, this.seed + 8L) % 100;
      return r < 12 ? 7 : (r < 22 ? 2 : 1);
   }

   private int roomDoor(int along, int center, int r, int frame) {
      int d = Math.abs(along - center);
      if (d <= 3 && r <= 7) {
         return 0;
      } else {
         return d <= 4 && r <= 8 ? frame : -1;
      }
   }

   private int roomLayer(int k, int r, int x, int y, int z, int ix, int iz, int ax, int az) {
      if (ax > 4 && az > 4) {
         int quad = (ix > 0 ? 1 : 0) + (iz > 0 ? 2 : 0);
         int id = this.roomId(k, quad);
         if (az == 5) {
            for (int i = 0; i < this.n; i++) {
               int d = this.roomDoor(ax, 6 + i * this.stride + this.RC, r, this.isPadRoom(k, i, 0) ? 3 : 6);
               if (d != -1) {
                  return d;
               }
            }

            return this.wall(x, y, x, y, z, this.roomColor(id));
         } else if (ax == 5) {
            for (int j = 0; j < this.n; j++) {
               int d = this.roomDoor(az, 6 + j * this.stride + this.RC, r, this.isPadRoom(k, 0, j) ? 3 : 6);
               if (d != -1) {
                  return d;
               }
            }

            return this.wall(z, y, x, y, z, this.roomColor(id));
         } else {
            return this.rooms(k, id, ax - 6, az - 6, r, x, y, z);
         }
      } else {
         return 0;
      }
   }

   private int roomId(int k, int quad) {
      return 100 + k * 4 + quad;
   }

   private boolean isPadRoom(int k, int i, int j) {
      return k >= this.K - 1 ? false : i == this.n - 1 && j == ((k & 1) == 0 ? this.n - 1 : 0);
   }

   private boolean padHole(int k, int ax, int az) {
      if (k >= this.K - 1) {
         return false;
      } else if (ax >= 6 && az >= 6 && ax <= this.H && az <= this.H) {
         int lx = ax - 6;
         int lz = az - 6;
         int i = lx / this.stride;
         int j = lz / this.stride;
         int ri = lx % this.stride;
         int rj = lz % this.stride;
         return i < this.n && j < this.n && ri < this.R && rj < this.R
            ? this.isPadRoom(k, i, j) && Math.abs(ri - this.RC) <= 1 && Math.abs(rj - this.RC) <= 1
            : false;
      } else {
         return false;
      }
   }

   private CuboPhoraShape.Layout layoutFor(int id) {
      CuboPhoraShape.Layout m = this.mazes.get(id);
      if (m != null) {
         return m;
      }

      int k = (id - 100) / 4;
      m = new CuboPhoraShape.Layout();
      m.oe = new boolean[this.n][this.n];
      m.on = new boolean[this.n][this.n];
      Random rnd = new Random(this.seed * 1000003L + id * 7919L + 17L);
      boolean[][] seen = new boolean[this.n][this.n];
      ArrayDeque<int[]> st = new ArrayDeque<>();
      int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
      st.push(new int[]{this.i0, 0});
      seen[this.i0][0] = true;

      while (!st.isEmpty()) {
         int[] c = st.peek();
         int[] order = new int[]{0, 1, 2, 3};

         for (int a = 3; a > 0; a--) {
            int b = rnd.nextInt(a + 1);
            int t = order[a];
            order[a] = order[b];
            order[b] = t;
         }

         boolean moved = false;

         for (int o : order) {
            int nx = c[0] + dirs[o][0];
            int ny = c[1] + dirs[o][1];
            if (nx >= 0 && ny >= 0 && nx < this.n && ny < this.n && !seen[nx][ny]) {
               carve(m, c[0], c[1], nx, ny);
               seen[nx][ny] = true;
               st.push(new int[]{nx, ny});
               moved = true;
               break;
            }
         }

         if (!moved) {
            st.pop();
         }
      }

      for (int i = 0; i < this.n; i++) {
         for (int j = 0; j < this.n; j++) {
            if (i + 1 < this.n && !m.oe[i][j] && rnd.nextInt(100) < 35) {
               m.oe[i][j] = true;
            }

            if (j + 1 < this.n && !m.on[i][j] && rnd.nextInt(100) < 35) {
               m.on[i][j] = true;
            }
         }
      }

      int[][] dist = new int[this.n][this.n];

      for (int[] row : dist) {
         Arrays.fill(row, -1);
      }

      ArrayDeque<int[]> q = new ArrayDeque<>();
      q.add(new int[]{this.i0, 0});
      dist[this.i0][0] = 0;
      int best = -1;
      m.gi = 0;
      m.gj = 0;

      while (!q.isEmpty()) {
         int[] c = q.poll();
         boolean hole = this.isPadRoom(k, c[0], c[1]) || k > 0 && this.isPadRoom(k - 1, c[0], c[1]);
         if (dist[c[0]][c[1]] > best && !hole) {
            best = dist[c[0]][c[1]];
            m.gi = c[0];
            m.gj = c[1];
         }

         for (int[] dd : dirs) {
            int nx = c[0] + dd[0];
            int ny = c[1] + dd[1];
            if (nx >= 0 && ny >= 0 && nx < this.n && ny < this.n && dist[nx][ny] == -1 && connected(m, c[0], c[1], nx, ny)) {
               dist[nx][ny] = dist[c[0]][c[1]] + 1;
               q.add(new int[]{nx, ny});
            }
         }
      }

      this.mazes.put(id, m);
      return m;
   }

   private static void carve(CuboPhoraShape.Layout m, int x0, int y0, int x1, int y1) {
      if (x1 == x0 + 1) {
         m.oe[x0][y0] = true;
      } else if (x1 == x0 - 1) {
         m.oe[x1][y0] = true;
      } else if (y1 == y0 + 1) {
         m.on[x0][y0] = true;
      } else {
         m.on[x0][y1] = true;
      }
   }

   private static boolean connected(CuboPhoraShape.Layout m, int x0, int y0, int x1, int y1) {
      if (x1 == x0 + 1) {
         return m.oe[x0][y0];
      } else if (x1 == x0 - 1) {
         return m.oe[x1][y0];
      } else {
         return y1 == y0 + 1 ? m.on[x0][y0] : m.on[x0][y1];
      }
   }

   private int solid(int id, int r, int x, int y, int z) {
      int h = hash(x, y, z, this.seed + 61L) % 100;
      if (r == 8 && h < 14) {
         return this.roomColor(id);
      } else {
         return h < 55 ? 1 : (h < 80 ? 2 : 7);
      }
   }

   private int rooms(int k, int id, int lx, int lz, int r, int x, int y, int z) {
      CuboPhoraShape.Layout m = this.layoutFor(id);
      int i = lx / this.stride;
      int ri = lx % this.stride;
      int j = lz / this.stride;
      int rj = lz % this.stride;
      if (i < this.n && j < this.n) {
         boolean rx = ri < this.R;
         boolean rz = rj < this.R;
         if (rx && rz) {
            return this.room(k, id, m, i, j, ri, rj, r, x, y, z);
         }

         if (rx) {
            boolean open = j < this.n - 1 && m.on[i][j] && Math.abs(ri - this.RC) <= 2;
            return open && r < 7 ? 0 : this.corridorSolid(id, open, rj, r, x, y, z);
         }

         if (!rz) {
            return this.solid(id, r, x, y, z);
         }

         boolean open = i < this.n - 1 && m.oe[i][j] && Math.abs(rj - this.RC) <= 2;
         return open && r < 7 ? 0 : this.corridorSolid(id, open, ri, r, x, y, z);
      } else {
         return this.solid(id, r, x, y, z);
      }
   }

   private int corridorSolid(int id, boolean isCorridor, int along, int r, int x, int y, int z) {
      return isCorridor && r == 7 && Math.floorMod(along, 4) == 2 ? this.roomColor(id) : this.solid(id, r, x, y, z);
   }

   private int room(int k, int id, CuboPhoraShape.Layout m, int i, int j, int a, int b, int r, int x, int y, int z) {
      int c = this.RC;
      int rc = this.isPadRoom(k, i, j) ? 3 : this.roomColor(id * 16 + i * 4 + j);
      boolean edge = a == 0 || a == this.R - 1 || b == 0 || b == this.R - 1;
      if (edge) {
         if (r < 7) {
            if (a == 0) {
               if (i > 0 && m.oe[i - 1][j] && Math.abs(b - c) <= 2) {
                  return 0;
               }

               if (i == 0 && Math.abs(b - c) <= 3 && r <= 7) {
                  return 0;
               }
            }

            if (a == this.R - 1 && i < this.n - 1 && m.oe[i][j] && Math.abs(b - c) <= 2) {
               return 0;
            }

            if (b == 0) {
               if (j > 0 && m.on[i][j - 1] && Math.abs(a - c) <= 2) {
                  return 0;
               }

               if (j == 0 && Math.abs(a - c) <= 3 && r <= 7) {
                  return 0;
               }
            }

            if (b == this.R - 1 && j < this.n - 1 && m.on[i][j] && Math.abs(a - c) <= 2) {
               return 0;
            }
         }

         return this.solid(rc, r, x, y, z);
      } else {
         boolean padRoom = this.isPadRoom(k, i, j);
         boolean inShaft = Math.abs(a - c) <= 2 && Math.abs(b - c) <= 2;
         if (padRoom && inShaft) {
            if (r == 0 && Math.abs(a - c) <= 1 && Math.abs(b - c) <= 1) {
               return 17;
            } else {
               return r == 0 && Math.abs(a - c) == 2 && Math.abs(b - c) == 2 ? 3 : 0;
            }
         } else {
            if (k > 0 && this.isPadRoom(k - 1, i, j) && r == 0 && Math.max(Math.abs(a - c), Math.abs(b - c)) == 2) {
               return 3;
            }

            if (r >= 10) {
               return r == 10 && (a & 3) == 3 && (b & 3) == 3 ? rc : this.solid(rc, r, x, y, z);
            }

            boolean px = a >= 1 && a <= 2 || a >= this.R - 3 && a <= this.R - 2;
            boolean pz = b >= 1 && b <= 2 || b >= this.R - 3 && b <= this.R - 2;
            if (px && pz) {
               return r == 9 ? rc : (r != 0 && r != 4 ? 1 : 2);
            }

            if (i == m.gi && j == m.gj && Math.abs(a - c) <= 1 && Math.abs(b - c) <= 1) {
               if (r == 0) {
                  return 5;
               }

               if (a == c && b == c && r <= 3) {
                  return 3;
               }
            }

            if (r == 0 && (i != m.gi || j != m.gj) && (a == 3 || a == this.R - 4) && Math.abs(b - c) <= 1 && (i + j) % 2 == 0) {
               return 2;
            }

            int feature = this.featureKind(id, i, j, m);
            if (feature != 0) {
               int f = this.featureBlock(feature, id, i, j, a, b, r, x, y, z);
               if (f != -1) {
                  return f;
               }
            }

            return 0;
         }
      }
   }

   private int featureKind(int id, int i, int j, CuboPhoraShape.Layout m) {
      int k = (id - 100) / 4;
      if (!this.isPadRoom(k, i, j) && (i != m.gi || j != m.gj)) {
         if (k > 0 && this.isPadRoom(k - 1, i, j)) {
            return 0;
         }

         int h = hash(id, i * 7 + 3, j, this.seed + 1337L);
         return h % 100 >= 33 ? 0 : 1 + Math.floorMod(i * 3 + j * 5 + id, 3);
      } else {
         return 0;
      }
   }

   private int featureBlock(int feature, int id, int i, int j, int a, int b, int r, int x, int y, int z) {
      int c = this.RC;
      int da = Math.abs(a - c);
      int db = Math.abs(b - c);
      if (feature == 1) {
         return this.rubble(id, i, j, a, b, r, da, db, x, y, z);
      } else {
         return feature == 2 ? this.altar(r, da, db) : this.braziers(r, da, db);
      }
   }

   private int rubble(int id, int i, int j, int a, int b, int r, int da, int db, int x, int y, int z) {
      int h = hash(id, i, j, this.seed + 909L);
      int ca = (h & 1) == 0 ? 3 : this.R - 4;
      int cb = (h & 2) == 0 ? 3 : this.R - 4;
      int ra = Math.abs(a - ca);
      int rb = Math.abs(b - cb);
      if (r != 1 || (rb != 0 || ra != 3) && (ra != 0 || rb != 3)) {
         if (r >= 0 && r <= 2) {
            int rad = 3 - r;
            if (ra <= rad && rb <= rad) {
               int v = hash(x, y, z, this.seed + 71L) % 100;
               if (v < 22) {
                  return 19;
               }

               if (v < 42) {
                  return 20;
               }

               return 18;
            }
         }

         if (a == ca && b == cb && r >= 3 && r <= 4) {
            return 20;
         } else {
            return r >= 6 && r <= 9 && ra == 2 && rb == 2 ? 21 : -1;
         }
      } else {
         return 20;
      }
   }

   private int altar(int r, int da, int db) {
      if (r == 0 && Math.max(da, db) <= 2) {
         return 18;
      } else if (r >= 1 && r <= 2 && Math.max(da, db) == 1) {
         return 22;
      } else if (r >= 1 && r <= 2 && da == 0 && db == 0) {
         return 1;
      } else if (r == 3 && da == 0 && db == 0) {
         return 22;
      } else if (r >= 0 && r <= 3 && da <= 1 && db == 2) {
         return 8;
      } else {
         return r >= 6 && r <= 9 && da == 3 && db == 3 ? 21 : -1;
      }
   }

   private int braziers(int r, int da, int db) {
      if (r == 0 && Math.max(da, db) == 2) {
         return 8;
      } else {
         boolean post = da == 3 && db == 0 || da == 0 && db == 3 || da == 3 && db == 3;
         if (post && r == 0) {
            return 18;
         } else if (post && r == 1) {
            return 8;
         } else {
            return post && r >= 7 && r <= 9 ? 21 : -1;
         }
      }
   }

   private static final class Layout {
      boolean[][] oe;
      boolean[][] on;
      int gi;
      int gj;
   }
}
