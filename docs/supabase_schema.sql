-- ============================================================================
-- SwahiLib Sprint 3 - Community Features Supabase Schema
-- ============================================================================
-- Run this in your Supabase project's SQL editor (Database > SQL Editor).
-- Local Room (core:database) stays the source of truth for single-player
-- progress; this schema only holds what needs to be shared between users
-- (profile snapshot, friendships, leaderboards, friend challenges, and an
-- achievement feed). SocialSyncWorker (core:social) pushes a lightweight
-- snapshot here periodically - it does not replace local storage.
-- ============================================================================

-- ── Profiles ────────────────────────────────────────────────────────────
-- One row per authenticated user, keyed to auth.users. Created on first
-- sign-in by SocialAuthRepo; kept in sync by SocialSyncWorker.
create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    display_name text not null,
    avatar_key text not null default 'default',
    level int not null default 1,
    total_xp int not null default 0,
    current_streak int not null default 0,
    friend_code text unique not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists profiles_total_xp_idx on public.profiles (total_xp desc);

alter table public.profiles enable row level security;

-- Anyone signed in can read any profile (needed for leaderboards/friend search).
create policy "profiles_select_all" on public.profiles
    for select using (auth.role() = 'authenticated');

-- Users can only write their own profile.
create policy "profiles_upsert_own" on public.profiles
    for insert with check (auth.uid() = id);

create policy "profiles_update_own" on public.profiles
    for update using (auth.uid() = id);

-- ── Friendships ─────────────────────────────────────────────────────────
create type public.friendship_status as enum ('pending', 'accepted', 'blocked');

create table if not exists public.friendships (
    id uuid primary key default gen_random_uuid(),
    requester_id uuid not null references public.profiles (id) on delete cascade,
    addressee_id uuid not null references public.profiles (id) on delete cascade,
    status public.friendship_status not null default 'pending',
    created_at timestamptz not null default now(),
    responded_at timestamptz,
    constraint friendships_no_self_friend check (requester_id <> addressee_id),
    constraint friendships_unique_pair unique (requester_id, addressee_id)
);

alter table public.friendships enable row level security;

create policy "friendships_select_own" on public.friendships
    for select using (auth.uid() = requester_id or auth.uid() = addressee_id);

create policy "friendships_insert_own" on public.friendships
    for insert with check (auth.uid() = requester_id);

create policy "friendships_update_participant" on public.friendships
    for update using (auth.uid() = requester_id or auth.uid() = addressee_id);

-- ── Weekly Competitions ─────────────────────────────────────────────────
-- One row per ISO week (period_key e.g. "2026-W31"), created lazily by the
-- app the first time a signed-in user opens the leaderboard that week.
create table if not exists public.weekly_competitions (
    id uuid primary key default gen_random_uuid(),
    period_key text unique not null,
    starts_at timestamptz not null,
    ends_at timestamptz not null,
    created_at timestamptz not null default now()
);

alter table public.weekly_competitions enable row level security;

create policy "weekly_competitions_select_all" on public.weekly_competitions
    for select using (auth.role() = 'authenticated');

create policy "weekly_competitions_insert_authenticated" on public.weekly_competitions
    for insert with check (auth.role() = 'authenticated');

-- ── Weekly Scores ───────────────────────────────────────────────────────
create table if not exists public.weekly_scores (
    competition_id uuid not null references public.weekly_competitions (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    xp_earned int not null default 0,
    updated_at timestamptz not null default now(),
    primary key (competition_id, user_id)
);

alter table public.weekly_scores enable row level security;

create policy "weekly_scores_select_all" on public.weekly_scores
    for select using (auth.role() = 'authenticated');

create policy "weekly_scores_upsert_own" on public.weekly_scores
    for insert with check (auth.uid() = user_id);

create policy "weekly_scores_update_own" on public.weekly_scores
    for update using (auth.uid() = user_id);

-- ── Friend Challenges ───────────────────────────────────────────────────
-- A duel: both participants play the same generated activity (an
-- ActivityType + difficulty + seed from core:games) and compare scores.
create type public.friend_challenge_status as enum ('pending', 'active', 'completed', 'declined', 'expired');

create table if not exists public.friend_challenges (
    id uuid primary key default gen_random_uuid(),
    challenger_id uuid not null references public.profiles (id) on delete cascade,
    opponent_id uuid not null references public.profiles (id) on delete cascade,
    activity_type text not null, -- mirrors core:engagement ActivityType name
    difficulty text not null,
    seed bigint not null, -- shared generator seed so both play the identical round
    challenger_score int,
    opponent_score int,
    status public.friend_challenge_status not null default 'pending',
    created_at timestamptz not null default now(),
    expires_at timestamptz not null default (now() + interval '3 days'),
    constraint friend_challenges_no_self check (challenger_id <> opponent_id)
);

alter table public.friend_challenges enable row level security;

create policy "friend_challenges_select_participant" on public.friend_challenges
    for select using (auth.uid() = challenger_id or auth.uid() = opponent_id);

create policy "friend_challenges_insert_challenger" on public.friend_challenges
    for insert with check (auth.uid() = challenger_id);

create policy "friend_challenges_update_participant" on public.friend_challenges
    for update using (auth.uid() = challenger_id or auth.uid() = opponent_id);

-- ── Achievement Feed ────────────────────────────────────────────────────
-- Mirrors a subset of local achievement_records (core:database) so friends
-- can see each other's unlocks. Written by SocialSyncWorker after a local
-- unlock; never written to directly by other users.
create table if not exists public.achievement_feed (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    achievement_id text not null, -- matches AchievementCatalog id
    unlocked_at timestamptz not null default now(),
    constraint achievement_feed_unique unique (user_id, achievement_id)
);

alter table public.achievement_feed enable row level security;

create policy "achievement_feed_select_all" on public.achievement_feed
    for select using (auth.role() = 'authenticated');

create policy "achievement_feed_insert_own" on public.achievement_feed
    for insert with check (auth.uid() = user_id);

-- ============================================================================
-- Setup notes for whoever runs this:
-- 1. Enable Google as an auth provider (or your preferred provider) under
--    Authentication > Providers - the app signs in via Credential Manager
--    and exchanges the Google ID token with Supabase Auth.
-- 2. Copy Project URL + anon public key into local.properties as
--    SUPABASE_URL / SUPABASE_ANON_KEY (see local.properties.example).
-- 3. `friend_code` on profiles should be generated client-side (short,
--    shareable, e.g. 6 alphanumeric chars) at profile-creation time -
--    SocialAuthRepo does this; retry on unique-constraint conflict.
-- ============================================================================
