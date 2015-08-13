package lite.httpsupport.impl;

public enum ThreadMode {
    Default {
        @Override
        public String getDescription() {
            return "默认模式，回调函数在子线程执行";
        }
    },
    Main {
        @Override
        public String getDescription() {
            return "回调函数在主线程执行";
        }
    };

    public abstract String getDescription();
}
