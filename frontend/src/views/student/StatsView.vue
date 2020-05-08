<template>
  <div class="container">
    <h3 align="left" data-cy="privacyInfo">
      Your Dashboard is set to
      {{ stats.publicDashboard ? 'PUBLIC' : 'PRIVATE' }}
    </h3>
    <v-btn
      v-model="checkedSwitch"
      inset
      class="privacyToggle"
      @click="togglePublicDashboard"
      data-cy="privacyButton"
      color="primary"
      >Change Privacy</v-btn
    >
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.totalQuizzes" />
        </div>
        <div class="project-name">
          <p>Total Quizzes Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.totalAnswers" />
        </div>
        <div class="project-name">
          <p>Total Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalUniqueQuestions">
          <animated-number :number="stats.totalUniqueQuestions" />
        </div>
        <div class="project-name">
          <p>Unique Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number :number="stats.correctAnswers">%</animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="improvedCorrectAnswers">
          <animated-number :number="stats.improvedCorrectAnswers"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Improved Correct Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number
            :number="
              (stats.totalUniqueQuestions * 100) / stats.totalAvailableQuestions
            "
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of questions seen</p>
        </div>
      </div>
    </div>

    <h2>Clarifications</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalClarificationQuestions">
          <animated-number
            :number="stats.totalClarificationQuestions"
            data-cy="totalClarificationQuestions"
          />
        </div>
        <div class="project-name">
          <p>Clarifications Created</p>
        </div>
      </div>
      <div class="items">
        <div
          class="icon-wrapper"
          ref="totalPublicClarificationQuestions"
          data-cy="totalPublicClarificationQuestions"
        >
          <animated-number :number="stats.totalPublicClarificationQuestions" />
        </div>
        <div class="project-name">
          <p>Clarifications marked as Public</p>
        </div>
      </div>
    </div>

    <h2>Tournaments</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalTournamentsCreated">
          <animated-number
            :number="stats.totalTournamentsCreated"
            data-cy="totalTournamentsCreated"
          />
        </div>
        <div class="project-name">
          <p>Tournaments Created</p>
        </div>
      </div>
      <div class="items">
        <div
          class="icon-wrapper"
          ref="totalTournamentsJoined"
          data-cy="totalTournamentsJoined"
        >
          <animated-number :number="stats.totalTournamentsJoined" />
        </div>
        <div class="project-name">
          <p>Tournaments Joined</p>
        </div>
      </div>
    </div>

  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StudentStats from '@/models/statement/StudentStats';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

@Component({
  components: { AnimatedNumber }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;
  checkedSwitch!: boolean;
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getUserStats();
      this.checkedSwitch = this.stats.publicDashboard;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getCurrentMessage(): string {
    if (this.stats)
      if (this.stats.publicDashboard)
        return 'Are you sure you want to set your Dashboard to Private?';
      else return 'Are you sure you want to set your Dashboard to Public?';
    return '';
  }

  async togglePublicDashboard() {
    if (confirm(this.getCurrentMessage())) {
      try {
        await RemoteServices.togglePublicDashboard();
        if (this.stats)
          this.stats.publicDashboard = !this.stats.publicDashboard;
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.privacyToggle {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  color: #1976d2;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
