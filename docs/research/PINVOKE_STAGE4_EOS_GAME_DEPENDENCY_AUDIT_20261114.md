# Stage 4 — Assembly-CSharp EOS Dependency Audit
Date: 2026-11-14
Evidence: user-supplied Termux output, `STAGE4_EOS_GAME_DEPENDENCY_AUDIT`, generated from `$HOME/PINVOKE_STAGE3_20261114/il_call_sites.csv`.

## Verified counts
- Assembly filter: `Assembly-CSharp.dll`.
- Native module filter: `EOSSDK-Win64-Shipping`.
- Linked EOS callsites: **382**.
- Unique managed caller-type / caller-method / native-entrypoint tuples: **382**.
- Distinct managed caller types: **33**.
- The 382 tuples each have count 1 in the emitted map; total and unique-tuple counts agree.
- All 382 linked calls are attributed to `Assembly-CSharp.dll` calling `EOSSDK-Win64-Shipping` in the previously supplied module/caller-assembly aggregation.
- Declaration/callsite reconciliation: 3,791 declarations across 66 exact module-name strings; 4,318 linked callsites across 65 exact module-name strings. `comdlg32.dll` is the only declaration-only module (2 declarations, 0 linked callsites). Case-only spellings remain distinct raw names and have not been normalized.

## Linked EOS calls by managed caller type
| Calls | Managed caller type |
|---:|---|
| 36 | `Epic.OnlineServices.Ecom.EcomInterface` |
| 29 | `Epic.OnlineServices.Lobby.LobbyInterface` |
| 28 | `Epic.OnlineServices.Platform.PlatformInterface` |
| 27 | `Epic.OnlineServices.Sessions.SessionsInterface` |
| 27 | `Epic.OnlineServices.Connect.ConnectInterface` |
| 25 | `Epic.OnlineServices.Achievements.AchievementsInterface` |
| 18 | `Epic.OnlineServices.Leaderboards.LeaderboardsInterface` |
| 16 | `Epic.OnlineServices.P2P.P2PInterface` |
| 16 | `Epic.OnlineServices.Lobby.LobbyDetails` |
| 12 | `Epic.OnlineServices.UserInfo.UserInfoInterface` |
| 12 | `Epic.OnlineServices.Auth.AuthInterface` |
| 11 | `Epic.OnlineServices.UI.UIInterface` |
| 11 | `Epic.OnlineServices.Presence.PresenceInterface` |
| 11 | `Epic.OnlineServices.PlayerDataStorage.PlayerDataStorageInterface` |
| 9 | `Epic.OnlineServices.TitleStorage.TitleStorageInterface` |
| 9 | `Epic.OnlineServices.Sessions.SessionModification` |
| 9 | `Epic.OnlineServices.Sessions.SessionSearch` |
| 9 | `Epic.OnlineServices.Lobby.LobbySearch` |
| 9 | `Epic.OnlineServices.Friends.FriendsInterface` |
| 8 | `Epic.OnlineServices.Sessions.SessionDetails` |
| 7 | `Epic.OnlineServices.Stats.StatsInterface` |
| 7 | `Epic.OnlineServices.Lobby.LobbyModification` |
| 6 | `Epic.OnlineServices.Presence.PresenceModification` |
| 5 | `Epic.OnlineServices.Sessions.ActiveSession` |
| 5 | `Epic.OnlineServices.Ecom.Transaction` |
| 4 | `Epic.OnlineServices.TitleStorage.TitleStorageFileTransferRequest` |
| 4 | `Epic.OnlineServices.PlayerDataStorage.PlayerDataStorageFileTransferRequest` |
| 3 | `Epic.OnlineServices.EpicAccountId` |
| 3 | `Epic.OnlineServices.ProductUserId` |
| 2 | `Epic.OnlineServices.Metrics.MetricsInterface` |
| 2 | `Epic.OnlineServices.Logging.LoggingInterface` |
| 1 | `Epic.OnlineServices.Helper` |
| 1 | `Epic.OnlineServices.ContinuanceToken` |
| **382** | **Total** |

## Interpretation and limits
- This is static IL linkage evidence: the managed assembly contains P/Invoke callsites into the EOS native module.
- The caller types shown are `Epic.OnlineServices.*` managed wrapper types. Therefore this establishes the EOS wrapper/native boundary inside `Assembly-CSharp.dll`; it does **not** yet prove which game-specific gameplay or UI classes call those wrappers.
- It does not prove that any listed method executes at runtime, that EOS is required for offline startup/gameplay, or that the audited Windows EOS binary is available/compatible on Android.
- The output contains a complete 382-row method-to-entrypoint listing, but this summary archive preserves the verified totals and per-type aggregation rather than reproducing all 382 rows. The original user-supplied `hhh.txt` was a single-line terminal transcript.

## Next evidence gate
Trace ordinary managed IL calls from non-wrapper game types into `Epic.OnlineServices.*` wrapper methods. This will distinguish wrapper declarations from game-level feature use. Keep runtime reachability and Android compatibility unclaimed until independently tested.

Research only; do not import or reuse third-party code, IL, binaries, or assets in CASTAWAY DEPTHS.
