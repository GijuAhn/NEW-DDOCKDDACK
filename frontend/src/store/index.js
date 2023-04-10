import { createStore } from "vuex";
import { memberStore } from "@/store/modules/memberStore";
import { commonStore } from "@/store/modules/commonStore";
import { mypageStore } from "@/store/modules/mypageStore";

export default createStore({
  modules: { commonStore, mypageStore, memberStore },
});
