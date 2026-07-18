Component({
  options: { addGlobalClass: true },
  properties: {
    title: { type: String, value: '加载失败' },
    desc: { type: String, value: '请检查网络后重试' },
    btnText: { type: String, value: '重新加载' },
  },
  methods: {
    onRetry() {
      this.triggerEvent('retry');
    },
  },
});
