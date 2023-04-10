<template>
  <div id="view">
    <bestcut-detail
      class="modal"
      v-if="detailBestcut"
      :bestcut="detailBestcut"
      @bestcutLike="(bestcutId) => bestcutLike(bestcutId)"
      @bestcutDislike="(bestcutId) => bestcutDislike(bestcutId)"
    ></bestcut-detail>
    <div id="searchBar">
      <div>
        <button id="btn-p" :class="tabP" @click="sortGames('P')">인기순</button>
        <button id="btn-r" :class="tabR" @click="sortGames('R')">최신순</button>
      </div>
      <div>
        <div class="choice" v-click-outside-element="offPeriodState">
          <div
            class="periodChoiced"
            @click="updatePeriodState"
            :class="periodRadius"
          >
            <div>
              <span>{{ period }}</span>
              <span class="arrow"></span>
            </div>
          </div>
          <div class="periodChoice" v-if="periodState === `on`">
            <div @click="updatePeriod(`전체기간`)">
              <span>전체기간</span>
            </div>
            <div @click="updatePeriod(`1일`)">
              <span>1일</span>
            </div>
            <div @click="updatePeriod(`1주`)">
              <span>1주</span>
            </div>
            <div @click="updatePeriod(`1개월`)">
              <span>1개월</span>
            </div>
            <div @click="updatePeriod(`6개월`)">
              <span>6개월</span>
            </div>
          </div>
        </div>
      </div>
      <div>
        <div class="choice" v-click-outside-element="offSearchState">
          <div
            class="searchChoiced"
            @click="updateSearchState"
            :class="searchRadius"
          >
            <div>
              <span>{{ search }}</span>
              <span class="arrow"></span>
            </div>
          </div>
          <div class="searchChoice" v-if="searchState === `on`">
            <div @click="updateSearch(`게임 제목`)">
              <span>게임 제목</span>
            </div>
            <div @click="updateSearch(`닉네임`)">
              <span>닉네임</span>
            </div>
          </div>
        </div>
      </div>
      <div>
        <input
          @keyup.enter="callApi"
          type="text"
          v-model.trim="pageConditionReq.keyword"
          placeholder="검색어를 입력해주세요"
        />
        <button id="btn-s" @click="callApi">검색</button>
      </div>
    </div>

    <div id="list">
      <normal-bestcut
        v-for="bestcut in bestcuts"
        :key="bestcut"
        :bestcut="bestcut"
        @bestcutDetail="detailBestcut = bestcut"
        @bestcutLike="(bestcutId) => bestcutLike(bestcutId)"
        @bestcutDislike="(bestcutId) => bestcutDislike(bestcutId)"
      ></normal-bestcut>
    </div>
    <div id="ifTotalPagesIsZero" v-if="totalPages === 0">
      <p>베스트컷 검색 결과가 없습니다.</p>
    </div>
    <page-nav
      :totalPageCount="totalPages"
      :value="pageConditionReq.page"
      @change="(num) => changePage(num)"
    ></page-nav>
  </div>
</template>

<script setup>
import PageNav from "@/components/common/PageNav.vue";
import NormalBestcut from "@/components/BestcutList/item/NormalBestcut.vue";
import BestcutDetail from "@/components/common/modal/BestcutDetailModal.vue";

import { apiInstance } from "@/api/index";
import { ref, computed, watch } from "vue";
import { useStore } from "vuex";

const api = apiInstance();
const store = useStore();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const tabP = ref("on");
const tabR = ref("off");
const period = ref("1개월");
const search = ref("게임 제목");
const periodState = ref("off");
const searchState = ref("off");
const periodRadius = ref();
const searchRadius = ref();

let bestcuts = ref();
let pageConditionReq = ref({
  order: "POPULARITY",
  period: "MONTH",
  search: "GAME",
  keyword: "",
  page: 1,
});
let totalPages = ref();
let detailBestcut = ref();

watch(periodState, () => {
  periodRadius.value = "period-radius-" + periodState.value;
});
watch(searchState, () => {
  searchRadius.value = "search-radius-" + searchState.value;
});
const updatePeriodState = () => {
  if (periodState.value === "on") periodState.value = "off";
  else periodState.value = "on";
};
const updateSearchState = () => {
  if (searchState.value === "on") searchState.value = "off";
  else searchState.value = "on";
};
const offPeriodState = () => {
  periodState.value = "off";
};
const offSearchState = () => {
  searchState.value = "off";
};
const updatePeriod = (option) => {
  period.value = option;
  if (option === "전체기간") {
    pageConditionReq.value.period = "ALL";
  } else if (option === "1일") {
    pageConditionReq.value.period = "DAY";
  } else if (option === "1주") {
    pageConditionReq.value.period = "WEEK";
  } else if (option === "1개월") {
    pageConditionReq.value.period = "MONTH";
  } else if (option === "6개월") {
    pageConditionReq.value.period = "HALF_YEAR";
  }

  updatePeriodState();
};
const updateSearch = (option) => {
  search.value = option;
  if (option === "게임 제목") {
    pageConditionReq.value.search = "GAME";
  } else if (option === "닉네임") {
    pageConditionReq.value.search = "MEMBER";
  }
  updateSearchState();
};

const callApi = () => {
  api
    .get(`/api/bestcuts`, {
      params: {
        order: pageConditionReq.value.order,
        period: pageConditionReq.value.period,
        search: pageConditionReq.value.search,
        keyword: pageConditionReq.value.keyword,
        page: pageConditionReq.value.page,
      },
      headers: { "access-token": accessToken },
    })
    .then((response) => {
      bestcuts.value = response.data.content;
      totalPages = response.data.totalPages;
    })
    .catch((error) => {
      console.log(error);
    });
};

callApi();

//정렬
const sortGames = (option) => {
  //변경 이벤트 발생시
  if (option === "P") {
    pageConditionReq.value.order = "POPULARITY";
    tabP.value = "on";
    tabR.value = "off";
  } else {
    pageConditionReq.value.order = "RECENT";
    tabP.value = "off";
    tabR.value = "on";
  }
  callApi();
};

//페이징 이동
const changePage = (page) => {
  pageConditionReq.value.page = page;
  callApi();
};

//베스트컷 좋아요
const bestcutLike = (bestcutId) => {
  if (!accessToken) {
    alert("로그인 후 이용해주세요.");
    return;
  }
  api
    .post(
      `/api/bestcuts/like/${bestcutId}`,
      {},
      { headers: { "access-token": accessToken } }
    )
    .then(() => {
      let bestcut = bestcuts.value.find((e) => e.bestcutId === bestcutId);
      bestcut.isLiked = true;
      bestcut.popularity++;
    })
    .catch((err) => {
      if (err.response.status === 401) {
        alert("로그인 후 이용해주세요.");
      }
    });
};

//베스트컷 좋아요 취소
const bestcutDislike = (bestcutId) => {
  api
    .delete(`/api/bestcuts/dislike/${bestcutId}`, {
      headers: { "access-token": accessToken },
    })
    .then(() => {
      let bestcut = bestcuts.value.find((e) => e.bestcutId === bestcutId);
      bestcut.isLiked = false;
      bestcut.popularity--;
    })
    .catch((err) => {
      if (err.response.status === 401) {
        alert("로그인 후 이용해주세요.");
      }
    });
};
</script>

<style scoped>
#view {
  border: 2px solid black;
  width: 1060px;
  position: relative;
  top: -320px;
  left: 50%;
  transform: translate(-50%, 0);
  background-color: white;
  padding: 70px;
  height: 1460px;
}
#searchBar {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row; /*수평 정렬*/
  align-items: center;
  justify-content: center;
  margin-bottom: 70px;
}
#searchBar > div {
  margin: auto;
}
#btn-p {
  margin-left: -15px;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: none;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  display: inline-block;
  height: 48px;
  width: 99px;
}
#btn-p:hover {
  cursor: pointer;
}
#btn-r {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  display: inline-block;
  height: 48px;
  width: 101px;
}
#btn-r:hover {
  cursor: pointer;
}

input {
  outline: none;
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: none;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  padding: 0 10px;
  height: 44px;
  width: 365px;
}

#btn-s {
  background-color: white;
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
  border-top: 2px solid black;
  border-left: 2px solid black;
  border-bottom: 2px solid black;
  border-right: 2px solid black;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  display: inline-block;
  height: 48px;
  background-color: #77a4cc;
  color: white;
  width: 80px;
  margin-right: -15px;
}
#btn-s:hover {
  cursor: pointer;
}

.choice {
  position: relative;
  font-size: 20px;
  font-family: "NanumSquareRoundB";
  height: 44px;
  line-height: 44px;
  cursor: pointer;
  display: inline;
}
.choice span {
  padding: 0 10px;
}
.periodChoiced > div,
.searchChoiced > div {
  width: 150px;
  border: 2px solid black;
  background-color: white;
  border-radius: 5px;
}
.periodChoice,
.searchChoice {
  z-index: 10;
  position: absolute;
  top: 48px;
  left: 0px;
}
.periodChoice > div,
.searchChoice > div {
  background-color: white;
  width: 150px;
  border-left: 2px solid black;
  border-right: 2px solid black;
  border-bottom: 2px solid black;
}
.periodChoice > div:hover,
.searchChoice > div:hover {
  background-color: #d9d9d9;
}
.periodChoice > div:last-child,
.searchChoice > div:last-child {
  border-radius: 0 0 5px 5px;
}
.period-radius-on > div,
.search-radius-on > div {
  border-radius: 5px 5px 0 0;
}

.on {
  background-color: #77a4cc;
  color: white;
}
.off {
  background-color: white;
  color: black;
}

#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 1090px;
}

.arrow {
  /* border: 1px solid red; */
  background-size: contain;
  background-repeat: no-repeat;
  width: 20px;
  height: 20px;
  background-image: url("@/assets/images/up-arrow.png");
  position: absolute;
  top: 50%;
  right: -5px;
  transform: translate(0, -50%);
}
.period-radius-on .arrow {
  background-image: url("@/assets/images/down-arrow.png");
}
.search-radius-on .arrow {
  background-image: url("@/assets/images/down-arrow.png");
}
#ifTotalPagesIsZero {
  top: 500px;
  left: 50%;
  transform: translate(-50%, 0);
  position: absolute;
  display: inline-block;
  text-align: center;
}
#ifTotalPagesIsZero p:first-child {
  font-size: 36px;
}
#nav {
  position: absolute;
  bottom: 70px;
  left: 50%;
  transform: translate(-50%, 0);
}
</style>
