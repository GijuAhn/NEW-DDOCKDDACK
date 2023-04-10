import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vueClickOutsideElement from "vue-click-outside-element";

createApp(App).use(store).use(router).use(vueClickOutsideElement).mount("#app");
