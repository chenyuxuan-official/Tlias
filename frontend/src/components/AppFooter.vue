<script setup>
// 页脚：公司名称 + 版权信息
</script>

<template>
  <div class="footer">
    <!-- 第一行：名称 -->
    <div class="company">山甫</div>
    <!-- 第二行：版权信息 -->
    <div class="copyright">版权所有 Copyright &copy; 2026 山甫 All Rights Reserved</div>
  </div>
</template>

<style scoped>
  .footer {
    /* 与上方卡片拉开距离 */
    margin-top: 20px;
    padding: 20px;
    /* 灰色背景，与顶栏同色，首尾呼应 */
    background-color: #4a4a4a;
    /* 白色字体 */
    color: #ffffff;
    /* 居中对齐 */
    text-align: center;
    font-size: 13px;
    /* 行高即两行之间的间距 */
    line-height: 24px;
  }

  /* 第一行：名称，加粗突出 */
  .footer .company {
    font-size: 14px;
    font-weight: bold;
    letter-spacing: 1px;
  }

  /* 第二行：版权信息，用浅灰弱化层次 */
  .footer .copyright {
    color: #cfcfcf;
  }
</style>
