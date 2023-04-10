export const commonStore = {
  namespaced: true,
  state: {
    view: "",
    tab: {
      admin: ["", ""],
      member: ["", "", "", ""],
    },
    currentModal: "",
  },
  getters: {},
  mutations: {
    setColor(state, value) {
      state.view = value;
    },
    setMemberTab(state, value) {
      for (let i = 0; i < 4; i++) {
        state.tab.member[i] = "off";
      }
      state.tab.member[value] = "on";
    },
    setAdminTab(state, value) {
      for (let i = 0; i < 2; i++) {
        state.tab.admin[i] = "off";
      }
      state.tab.admin[value] = "on";
    },
    setCurrentModal(state, value) {
      state.currentModal = value;
    },
  },
  actions: {
    setColorAsync({ commit }, value) {
      commit("setColor", value);
    },
    setMemberTabAsync({ commit }, value) {
      commit("setMemberTab", value);
    },
    setAdminTabAsync({ commit }, value) {
      commit("setAdminTab", value);
    },
    setCurrentModalAsync({ commit }, value) {
      commit("setCurrentModal", value);
    },
  },
};
