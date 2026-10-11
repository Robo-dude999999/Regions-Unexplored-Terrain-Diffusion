<img width="1920" height="1009" alt="2026-10-10_22-51-12-png" src="https://github.com/user-attachments/assets/29f56f86-8a2d-434b-abf8-5af2d5aa850b" />
Fixes the incompatibilities between the terrain diffusion minecraft integration and regions unexplored.

Most biomes for regions unexplored are currently unsupported, and will be added over the course of the near future. Some may depend on updates to terrain diffusion, such as river biomes.

Coming soon:
  - Fabric version
  - 1.21.11 neoforge version

Terrain diffusion is a neural network designed to generate realistic terrain and climate information based off real world data. In some cases mountains can be over 1000 blocks tall, and caves super deep. The generation is insane, but only uses vanilla biomes out of the box. Also, since it overrides the entire vanilla world generation system, no standard biome mods are compatible. I saw a reddit post where Comrade_Cydra made an experimental mod with chat GPT to integrate biomes o plenty into terrain diffusion. This inspired me to do the same, and here it is.

Chat GPT was used for some of the mixin initalization code.
