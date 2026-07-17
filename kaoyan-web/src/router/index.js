import { createRouter, createWebHistory } from 'vue-router'
import CardList from '../views/CardList.vue'
import CardReview from '../views/CardReview.vue'
import CardForm from '../views/CardForm.vue'
import MistakeList from '../views/MistakeList.vue'
import MistakeReview from '../views/MistakeReview.vue'
import MistakeForm from '../views/MistakeForm.vue'
import DailyPlan from '../views/DailyPlan.vue'
import FocusTimer from '../views/FocusTimer.vue'
import FocusStats from '../views/FocusStats.vue'
import KnowledgeGraph from '../views/KnowledgeGraph.vue'
import KnowledgeNodeList from '../views/KnowledgeNodeList.vue'
import KnowledgeReview from '../views/KnowledgeReview.vue'
import KnowledgeRecommend from '../views/KnowledgeRecommend.vue'
import FileManager from '../views/FileManager.vue'

const routes = [
  { path: '/', redirect: '/cards' },
  { path: '/cards', component: CardList },
  { path: '/review', component: CardReview },
  { path: '/cards/new', component: CardForm },
  { path: '/cards/edit/:id', component: CardForm },
  { path: '/mistakes', component: MistakeList },
  { path: '/mistakes/review', component: MistakeReview },
  { path: '/mistakes/new', component: MistakeForm },
  { path: '/mistakes/edit/:id', component: MistakeForm },
  { path: '/plan', component: DailyPlan },
  { path: '/focus', component: FocusTimer },
  { path: '/focus/stats', component: FocusStats },
  { path: '/knowledge/graph', component: KnowledgeGraph },
  { path: '/knowledge/nodes', component: KnowledgeNodeList },
  { path: '/knowledge/review', component: KnowledgeReview },
  { path: '/knowledge/recommend', component: KnowledgeRecommend },
  { path: '/files', component: FileManager }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
