package workload;

class DynamicArrayTest extends IntListContract {
    @Override
    protected IntList create() { return new DynamicArray(); }
}