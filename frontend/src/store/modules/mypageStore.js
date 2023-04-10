import router from "@/router";
import {
  getMyBestcut,
  getMygame,
  getRecentGame,
  getStarGame,
} from "@/api/mypage";

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
      console.log(userid, "  ", pageConditionReq);
      await getMyBestcut(
        userid,
        pageConditionReq,
        accessToken,
        ({ data }) => {
          console.log(data.content, "^^");
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
      await getMygame(
        pageCondition,
        accessToken,
        ({ data }) => {
          if (data.status === 200) {
            console.log("getMemberInfo data >> ", data);
            commit("SET_MEMBER_INFO", data.memberInfo);
          } else {
            console.log("유저 정보 없음!!!!");
          }
        },
        async (error) => {
          console.log(error.response.status);
        }
      );
    },
    async getRecentGameList({ commit, state }, pageConditionReq) {
      let accessToken = state.accessToken;
      await getRecentGame(
        pageConditionReq,
        accessToken,
        ({ data }) => {
          if (data.status === 200) {
            let accessToken = data.accessToken;
            console.log("재발급 완료 >> 새로운 토큰 : {}", accessToken);
            commit("SET_ACCESSTOKEN", data.memberInfo);
            commit("SET_IS_VALID_TOKEN", true);
          }
        },
        async (error) => {
          //AccessToken 갱신 실패시 refreshToken이 문제임 >> 다시 로그인해야함
          if (error.reembersponse.status === 401) {
            console.log("갱신 실패");
            await getRecentGame(
              state.memberInfo.id,
              ({ data }) => {
                if (data.status === 200) {
                  console.log("리프레시 토큰 제거 성공");
                } else {
                  console.log("리프레시 토큰 제거 실패");
                }
                alert("RefreshToken 기간 만료!!! 다시 로그인해 주세요.");
                commit("SET_IS_LOGIN", false);
                commit("SET_MEMBER_INFO", null);
                commit("SET_IS_VALID_TOKEN", false);
                router.push({ name: "login" });
              },
              (error) => {
                console.log(error);
                commit("SET_IS_LOGIN", false);
                commit("SET_MEMBER_INFO", null);
              }
            );
          }
        }
      );
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
        } else {
          console.log("유저 정보 없음!!!!");
        }
      },
      (error) => {
        console.log(error);
      }
    );
  },
};
