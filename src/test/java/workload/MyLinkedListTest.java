package workload;

class MyLinkedListTest extends IntListContract {
    @Override
    protected IntList create() { return new MyLinkedList(); }
}