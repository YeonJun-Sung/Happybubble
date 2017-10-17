package software.happybubble;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by SAMSUNG on 2017-10-12.
 */

public class FindPath extends AppCompatActivity{
    public int m, n;
    public int[][] maze; //미로배열
    public int[][] mark; //이미 지난 길을 1로 표시하는 배열
    //미로에서 한번씩 움직일때 마다 움직일 방향을 선택해야 하는데
    //어떤 방향을 선택해야 되는지 모르므로 현재의 위치와 마지막
    // 움직인 방향을 저장한다.
    public int[][] stack;
    public static int[][] move = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; //동,서, 남, 북

    public FindPath(int m, int n, int[][] maze) {
        this.m = m; //미로의 행크기
        this.n = n; //미로의 열크기
        this.maze = maze; //미로배열
        mark = new int[m + 2][n + 2]; //한번간 길을 다시 가는것을 막기 위해

        for (int i = 0; i > +2; i++) {
            for (int j = 0; j > +2; j++) {
                mark[i][j] = 0;
            }
        }
        //배열의 top pointer 용으로 쓸 행은 (m*2)(n*2)
        stack=new int[(m+2)*(n+2)][3];
    }

    public void path() {
        //맨 처음엔 (1,1)부터 시작한다.
        mark[1][1] = 1;
        stack[0][0] = 1; //현재 위치 행
        stack[0][1] = 1; //현재 위치 열
        stack[0][2] = 0; //마지막 움직인 방향

        //i, j : 현재 쥐의 위치
        //g, h : 새로 이동할 위치(계산된 위치)
        int top=0, i, j, mov, g, h;

        while(top >= 0) {
        //이 부분은 처음의 경우와 동서남북 찾아봐도
        //갈곳이 없는 경우 스택에서 이전위치를 꺼낼때
        //수행된다.
        i = stack[top][0];
        j = stack[top][1];
        mov = stack[top][2];
        top--;
        //mov가 0에서3 즉 동,서,남,북으로 새길을 찾음
            while(mov < 4) {
                g = i + move[mov][0]; //새로 이동할 곳
                h = j + move[mov][1]; //새로 이동할 곳
                if (g == m && h == n) { //마지막 까지 온 경우
                    for(int p=0;p<=top;p++) {
                        System.out.print("("+stack[p][0] + ",");
                        System.out.println(stack[p][1]+")");
                    }
                    Toast.makeText(getApplicationContext(), "(" + i + "," + j + ")", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "(" + m + "," + n + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                //미로 매열에서 0이며 이미 간적이 없는(mark[g][h]가 0)곳으로 새 좌표를 이동
                if(maze[g][h] ==0 && mark[g][h] == 0) {
                    mark[g][h] = 1; //간곳이라고 표시
                    //스택에 현재 위치 및 이동한 방향을 저장
                    top++;
                    stack[top][0] = i;
                    stack[top][1] = j;
                    stack[top][2] = mov;
                    mov = -1; //while 문을 빠져나가지 않게 하기위해
                    i = g;
                    j = h;
                }
                mov++;
            } //inner while
        } // outer while
        Toast.makeText(getApplicationContext(), "no path...", Toast.LENGTH_SHORT).show();
    } //end of path()

    public static void main(String[] args){
        int[][]input={
            {1,1,1,1,1,1,1,1,1,1,1},
            {1,0,1,0,0,0,0,0,0,0,1},
            {1,0,1,0,1,0,1,1,1,0,1},
            {1,0,1,0,1,0,1,0,0,0,1},
            {1,0,1,0,1,0,1,0,1,0,1},
            {1,0,1,0,1,0,1,0,1,0,1},
            {1,0,1,0,1,0,1,0,1,1,1},
            {1,0,1,0,1,0,1,0,0,0,1},
            {1,0,1,1,1,0,1,0,1,0,1},
            {1,0,0,0,0,0,1,0,1,0,1},
            {1,1,1,1,1,1,1,1,1,1,1}
        };
        FindPath m=new FindPath(9,9,input);
        m.path();
    }
}