<template>
  <div id="view" v-if="games">
    <div id="searchBar">
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
    <div id="list" v-if="isShow">
      <single-game
        v-for="(game, index) in games"
        :key="index"
        :game="game"
        :index="index"
        @ready="(value) => ready(value)"
      ></single-game>
    </div>

    <div id="container" v-show="!isShow">
      <img :src="`${IMAGE_PATH}/${games[targetGameIdx].thumbnail}`" />
      <video autoplay="true" id="videoElement"></video>
    </div>

    <div id="ifTotalPagesIsZero" v-if="totalPages === 0">
      <p>게임 검색 결과가 없습니다.</p>
    </div>
    <page-nav
      :totalPageCount="totalPages"
      :value="pageConditionReq.page"
      @change="(num) => changePage(num)"
    ></page-nav>
  </div>
</template>

<script setup>
import { apiInstance } from "@/api/index";
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useStore } from "vuex";
import SingleGame from "@/components/GameList/item/SingleGame.vue";
import PageNav from "@/components/common/PageNav.vue";

const api = apiInstance();
const store = useStore();
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const games = ref();
const isShow = ref(true);
const targetGameIdx = ref(0);
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;

let totalPages = ref();

onMounted(() => {});

onBeforeUnmount(() => {
  const video = document.getElementById("videoElement");
  if (video) {
    if (video.srcObject) {
      video.srcObject.getTracks().forEach((track) => {
        track.stop();
      });
    }
  }
});

//페이징 이동
const changePage = (page) => {
  pageConditionReq.value.page = page;
  callApi();
};

let pageConditionReq = ref({
  order: "POPULARITY",
  period: "ALL",
  search: "GAME",
  keyword: "",
  page: 1,
});

const ready = (value) => {
  targetGameIdx.value = value.index;
  isShow.value = false;
  console.log(games.value[targetGameIdx.value]);
  const video = document.getElementById("videoElement");
  if (navigator.mediaDevices.getUserMedia) {
    navigator.mediaDevices
      .getUserMedia({ video: true, audio: false })
      .then(function (stream) {
        video.srcObject = stream;
      })
      .catch((err) => {
        console.log(err);
        console.log("Something went wrong!");
      });
  }
};

const callApi = () => {
  api
    .get(`/api/games`, {
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
      games.value = response.data.content;
      totalPages = response.data.totalPages;
    })
    .catch((error) => {
      console.log(error);
    });
};

callApi();
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
  margin-left: -10px;
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
  margin-right: -10px;
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
  background-color: #f08383;
  color: white;
  width: 80px;
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
.searchChoice > div {
  background-color: white;
  width: 150px;
  border-left: 2px solid black;
  border-right: 2px solid black;
  border-bottom: 2px solid black;
}
.searchChoice > div:hover {
  background-color: #d9d9d9;
}
.searchChoice > div:last-child {
  border-radius: 0 0 5px 5px;
}
.period-radius-on > div,
.search-radius-on > div {
  border-radius: 5px 5px 0 0;
}

.on {
  background-color: #f08383;
  color: white;
}
.off {
  background-color: white;
  color: black;
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
.search-radius-on .arrow {
  background-image: url("@/assets/images/down-arrow.png");
}
#list {
  display: grid;
  gap: 35px 0;
  grid-template-columns: repeat(3, 1fr);
  width: 1090px;
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

#container {
  margin: 0px auto;
  width: 500px;
  height: 375px;
  border: 10px #333 solid;
}
#videoElement {
  width: 500px;
  height: 375px;
  background-color: #666;
}
</style>
