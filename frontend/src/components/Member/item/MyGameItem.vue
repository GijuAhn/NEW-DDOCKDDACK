<template>
  <div id="content">
    <div id="topSection">
      <img
        :src="`${IMAGE_PATH}/${props.game.thumbnail}`"
        alt="대표사진"
        class="image"
      />
      <div class="imageBehind">
        <div class="count">
          <span>
            <span>
              <img
                :src="require(`@/assets/images/play.png`)"
                width="14"
                height="14" /></span
            ><span>{{ props.game.popularity }} </span> </span
          ><span>
            <span>
              <img
                :src="require(`@/assets/images/star.png`)"
                width="14"
                height="14" /></span
            ><span>{{ props.game.starredCnt }} </span>
          </span>
        </div>
        <div class="creator">
          <span>made by. {{ props.game.creator }}</span>
        </div>
      </div>
    </div>
    <div id="bottomSection">
      <div id="gameTitle">
        <span>{{ props.game.gameTitle }}</span>
      </div>
      <div id="gameDesc">
        <span>{{ props.game.gameDesc }}</span>
      </div>
      <div id="createRoomButton">
        <button @click="createSession(props.game.gameId)">
          <span class="play-btn"></span>
          &nbsp;방 생성
        </button>
      </div>
      <div id="etcSection" v-click-outside-element="onClickOutside">
        <div id="etcButton" @click="open">
          <img
            :src="require(`@/assets/images/etc-button.png`)"
            alt="기타버튼"
            class="etc"
          />
        </div>
        <div id="etc" v-show="state">
          <div v-if="props.game.isStarred === 0" @click="starredGame">
            <span>즐겨찾기</span>
          </div>
          <div v-if="props.game.isStarred === 1" @click="unstarredGame">
            <span>즐겨찾기 해제</span>
          </div>
          <!-- <div><span>베스트 컷</span></div> -->
          <div @click="setCurrentModalAsync(`preview`)">
            <span>문제 미리보기</span>
          </div>
          <div @click="setCurrentModalAsync(`reportReason`)">
            <span>신고</span>
          </div>
          <div @click="deleteGame">
            <span>삭제</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, onMounted, ref, computed } from "vue";
import { useStore } from "vuex";
import { apiInstance } from "@/api/index";
import router from "@/router/index.js";
import process from "process";

const store = useStore();
const emit = defineEmits(["updateProps"]);
const props = defineProps(["game", "index"]);
const api = apiInstance();
const IMAGE_PATH = process.env.VUE_APP_IMAGE_PATH;
const accessToken = computed(() => store.state.memberStore.accessToken).value;
const onClickOutside = () => {
  state.value = false;
};

const open = () => {
  state.value = !state.value;
};

const state = ref(false);

const setCurrentModalAsync = (what) => {
  open();
  store.dispatch("commonStore/setCurrentModalAsync", {
    name: what,
    data: props.game,
  });
};
onMounted(() => {
  // console.log(process.env);
});

const createSession = (gameId) => {
  api
    .post(
      "/api/game-rooms",
      {
        gameId,
      },
      {
        headers: {
          "access-token": accessToken,
        },
      }
    )
    .then((res) => {
      router.replace(`/gameroom/${res.data}`);
    });
};

const starredGame = () => {
  open();
  api
    .post(
      `/api/games/starred/${props.game.gameId}`,
      {},
      { headers: { "access-token": accessToken } }
    )
    .then(() => {
      emit("updateProps", { status: "starred", index: props.index });
    })
    .catch((error) => {
      error;
      alert("로그인이 필요한 기능입니다.");
    });
};
const unstarredGame = () => {
  open();
  api
    .delete(`/api/games/unstarred/${props.game.gameId}`, {
      headers: { "access-token": accessToken },
    })
    .then(() => {
      emit("updateProps", { status: "unstarred", index: props.index });
    })
    .catch((error) => {
      error;
      alert("로그인이 필요한 기능입니다.");
    });
};
const deleteGame = () => {
  const result = confirm("삭제 하시겠습니까?");
  if (result) {
    api
      .delete(`/api/games/${props.game.gameId}`, {
        headers: { "access-token": accessToken },
      })
      .then(() => {
        alert("삭제 되었습니다.");
        emit("updateProps", { index: props.index });
      })
      .catch(() => {
        alert("삭제 실패");
      });
  }
};
</script>

<style scoped>
#content {
  border: 2px solid black;
  width: 325px;
  height: 380px;
}
#createRoomButton {
  margin-top: 10px;
}
#gameTitle {
  width: 95%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#gameDesc {
  margin-top: 5px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#createRoomButton button {
  border: 1px solid #da6c6b;
  background-color: white;
  font-size: 14px;
  font-family: "NanumSquareRoundB";
  width: 100%;
  padding: 10px 0;
  color: #da6c6b;
}
#createRoomButton button:hover {
  color: white;
  background-color: #da6c6b;
  cursor: pointer;
  transition: 0.3s;
}
#createRoomButton button:hover .play-btn {
  background-image: url("@/assets/images/play.png");
}
.play-btn {
  display: inline-block;
  background-size: contain;
  background-repeat: no-repeat;
  width: 12px;
  height: 12px;
  vertical-align: middle;
  background-image: url("@/assets/images/play2.png");
}
#gameTitle span {
  font-size: 22px;
}
#gameDesc span {
  font-size: 16px;
  color: #656565;
}
#topSection {
  position: relative;
}
.image {
  width: 325px;
  height: 260px;
  object-fit: cover;
  display: inline-block;
  /* display: none; */
}
.image:hover {
  filter: brightness(50%);
}
.image:hover ~ .imageBehind {
  display: inline-block;
}
.imageBehind {
  width: 325px;
  height: 260px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  display: none;
}
.count {
  position: absolute;
  top: 50%;
  width: 325px;
  text-align: center;
}
.count > span {
  margin: 10px;
  font-size: 20px;
}
.count > span > span {
  margin: 5px;
}
.creator {
  width: 325px;
  position: absolute;
  right: 3px;
  bottom: 3px;
  text-align: right;
}
#bottomSection {
  padding: 5px 10px;
  position: relative;
}
#etcButton {
  position: absolute;
  top: 0px;
  left: 290px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  text-align: center;
}
#etcButton:hover {
  background-color: #d9d9d9;
  cursor: pointer;
  transition: 0.3s;
}
.etc {
  width: 50%;
  height: 50%;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
/* #content:hover {
    box-shadow: 0 0 20px #8b8b8b;
    transition: 0.3s;
  } */
#etc {
  position: absolute;
  top: 30px;
  left: 290px;
  background-color: white;
  width: 130px;
  box-shadow: 0 0 10px #8b8b8b;
  z-index: 1;
  border-radius: 10px;
  padding: 10px 0;
  display: block;
}
#etc :hover {
  cursor: pointer;
}
#etc span {
  font-size: 16px;
  display: block;
  padding: 10px;
}
#etc span:hover {
  background-color: #d9d9d9;
}
</style>
