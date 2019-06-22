    public class NothingSelectedSpinnerAdapter implements SpinnerAdapter, ListAdapter {

        protected static final int EXTRA = 1;
        protected SpinnerAdapter adapter;
        protected Context context;
        protected int nothingSelectedLayout;
        protected int nothingSelectedDropdownLayout;
        protected LayoutInflater layoutInflater;

        public NothingSelectedSpinnerAdapter(
                SpinnerAdapter spinnerAdapter,
                int nothingSelectedLayout, MainActivity context) {

            this(spinnerAdapter, nothingSelectedLayout, -1, context);
        }

        public NothingSelectedSpinnerAdapter(SpinnerAdapter spinnerAdapter,
                                             int nothingSelectedLayout, int nothingSelectedDropdownLayout, Context context) {
            this.adapter = spinnerAdapter;
            this.context = context;
            this.nothingSelectedLayout = nothingSelectedLayout;
            this.nothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public final View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                return getNothingSelectedView(parent);
            }
            return adapter.getView(position - EXTRA, null, parent); // Could re-use
        }

        protected View getNothingSelectedView(ViewGroup parent) {
            return layoutInflater.inflate(nothingSelectedLayout, parent, false);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                return nothingSelectedDropdownLayout == -1 ?
                        new View(context) :
                        getNothingSelectedDropdownView(parent);
            }


            return adapter.getDropDownView(position - EXTRA, null, parent);
        }

        protected View getNothingSelectedDropdownView(ViewGroup parent) {
            return layoutInflater.inflate(nothingSelectedDropdownLayout, parent, false);
        }

        @Override
        public int getCount() {
            int count = adapter.getCount();
            return count == 0 ? 0 : count + EXTRA;
        }

        @Override
        public Object getItem(int position) {
            return position == 0 ? null : adapter.getItem(position - EXTRA);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position >= EXTRA ? adapter.getItemId(position - EXTRA) : position - EXTRA;
        }

        @Override
        public boolean hasStableIds() {
            return adapter.hasStableIds();
        }

        @Override
        public boolean isEmpty() {
            return adapter.isEmpty();
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            adapter.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            adapter.unregisterDataSetObserver(observer);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return position != 0;
        }

    }
