<template>
  <div>
    <div id="reportModal">
      <div>
        <div>
          <img
            :src="require(`@/assets/images/check.png`)"
            width="40"
            height="40"
          />
        </div>
        <div><span>신고처리 되었습니다</span></div>
      </div>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
#reportModal {
  background-color: white;
  width: 235px;
  height: 220px;
  border-radius: 10px;
  padding: 5px 0;
  text-align: center;
}
#reportModal > div {
  margin: 50px 0;
}
#reportModal > div > div {
  margin: 30px 0;
}
</style>
