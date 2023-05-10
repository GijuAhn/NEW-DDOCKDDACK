import { createStore } from "vuex";
import { memberStore } from "@/store/modules/memberStore";
import { commonStore } from "@/store/modules/commonStore";
import { mypageStore } from "@/store/modules/mypageStore";
import createPersistedState from "vuex-persistedstate";

export default createStore({
  modules: { commonStore, mypageStore, memberStore },
  plugins: [
    createPersistedState({
      paths: ["memberStore"],
      key: "vuexStore",
      storage: window.sessionStorage,
    }),
  ],
});
