import { getMyBestcut, getMygame, getStarGame } from "@/api/mypage";

export const mypageStore = {
  namespaced: true,
  state: {
    memberInfo: null,

    myBestcutList: [],
    myGameList: [],
    recentGameList: [],
    starredList: [],
  },
  getters: {
    getAccessToken(state) {
      return state.accessToken;
    },
    getMyBestcut(state) {
      return state.myBestcutList;
    },
  },
  mutations: {
    setToken(state, value) {
      state.accessToken = value;
    },
    setMemberInfo(state, memberInfo) {
      state.memberInfo = memberInfo;
    },
    setMyBestcutList(state, getMyBestcutList) {
      state.myBestcutList = getMyBestcutList;
    },
    setMyGameList(state, getMyGameList) {
      state.myGameList = getMyGameList;
    },
    setRecentGameList(state, getRecentGameList) {
      state.recentGameList = getRecentGameList;
    },
    setStarGameList(state, starredList) {
      state.starredList = starredList;
    },
  },
  actions: {
    async getMyBestcutList({ commit, state }, { userid, pageConditionReq }) {
      let accessToken = state.accessToken;
      await getMyBestcut(
        userid,
        pageConditionReq,
        accessToken,
        ({ data }) => {
          if (data.status === 200) {
            commit("setMyBestcutList", data);
            return data;
            // sessionStorage.setItem("access-token", accessToken); //변수에
            // sessionStorage.setItem("refresh-token", refreshToken); //cookie에
          }
        },
        (error) => {
          console.log(error);
        }
      );
    },

    async getMyGameList({ commit, state }, pageCondition) {
      let accessToken = state.accessToken;
      await getMygame(pageCondition, accessToken, ({ data }) => {
        if (data.status === 200) {
          commit("SET_MEMBER_INFO", data.memberInfo);
        }
      });
    },
  },

  async getStarGameList({ commit, state }, id) {
    let accessToken = state.accessToken;
    await getStarGame(
      id,
      accessToken,
      ({ data }) => {
        if (data.status === 200) {
          commit("SET_IS_LOGIN", false);
          commit("SET_USER_INFO", null);
          commit("SET_IS_VALID_TOKEN", false);
        }
      },
      (error) => {
        console.log(error);
      }
    );
  },
};
