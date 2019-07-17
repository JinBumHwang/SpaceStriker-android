package hofio.spacestriker;

// 최대크기를 지정해놓고 쓰는 나만의 객체배열
// 배열 길이에 대한 예외처리 안함
public class MyList <T>{
    private T[] list;
    private int index=0;

    public MyList(T[] list) {
        this.list = list;
    }
    public T add() { return list[index++]; }
    public void remove(int index){
        this.index--;
        T tmp=list[index];
        for(int i=index;i<this.index;i++){
            list[i]=list[i+1];
        }
        list[this.index]=tmp;
    }
    public T get(int index){
        return list[index];
    }
    //  add(){list[index++];} 이것 덕분에 index로 반환함.
    public int size(){
        return index;
    }
    public void removeAll(){
        index=0;
    }
}
