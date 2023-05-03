import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vueClickOutsideElement from "vue-click-outside-element";
import VueGtag from "vue-gtag-next";

const app = createApp(App);

app.use(VueGtag, {
  property: { id: "G-ZJZ24GL16W" },
});

app.use(store).use(router).use(vueClickOutsideElement).mount("#app");
