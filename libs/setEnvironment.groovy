def call(CONFIG_PATH) {
   def util = new commonUtil()
   util.loadConfiguration(CONFIG_PATH)
   util.setBaseEnv()
}
