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

create policy "profiles_select_all" on public.profiles
    for select using (auth.role() = 'authenticated');

create policy "profiles_upsert_own" on public.profiles
    for insert with check (auth.uid() = id);

create policy "profiles_update_own" on public.profiles
    for update using (auth.uid() = id);

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

create table if not exists public.competitions (
    id uuid primary key default gen_random_uuid(),
    period_key text unique not null,
    starts_at timestamptz not null,
    ends_at timestamptz not null,
    created_at timestamptz not null default now()
);

alter table public.competitions enable row level security;

create policy "competitions_select_all" on public.competitions
    for select using (auth.role() = 'authenticated');

create policy "competitions_insert_authenticated" on public.competitions
    for insert with check (auth.role() = 'authenticated');

create table if not exists public.scores (
    competition_id uuid not null references public.competitions (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    xp_earned int not null default 0,
    updated_at timestamptz not null default now(),
    primary key (competition_id, user_id)
);

alter table public.scores enable row level security;

create policy "scores_select_all" on public.scores
    for select using (auth.role() = 'authenticated');

create policy "scores_upsert_own" on public.scores
    for insert with check (auth.uid() = user_id);

create policy "scores_update_own" on public.scores
    for update using (auth.uid() = user_id);

create type public.challenge_status as enum ('pending', 'active', 'completed', 'declined', 'expired');

create table if not exists public.challenges (
    id uuid primary key default gen_random_uuid(),
    challenger_id uuid not null references public.profiles (id) on delete cascade,
    opponent_id uuid not null references public.profiles (id) on delete cascade,
    activity_type text not null,
    difficulty text not null,
    seed bigint not null,
    challenger_score int,
    opponent_score int,
    status public.challenge_status not null default 'pending',
    created_at timestamptz not null default now(),
    expires_at timestamptz not null default (now() + interval '3 days'),
    constraint challenges_no_self check (challenger_id <> opponent_id)
);

alter table public.challenges enable row level security;

create policy "challenges_select_participant" on public.challenges
    for select using (auth.uid() = challenger_id or auth.uid() = opponent_id);

create policy "challenges_insert_challenger" on public.challenges
    for insert with check (auth.uid() = challenger_id);

create policy "challenges_update_participant" on public.challenges
    for update using (auth.uid() = challenger_id or auth.uid() = opponent_id);

create table if not exists public.achievements (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    achievement_id text not null,
    unlocked_at timestamptz not null default now(),
    constraint achievements_unique unique (user_id, achievement_id)
);

alter table public.achievements enable row level security;

create policy "achievements_select_all" on public.achievements
    for select using (auth.role() = 'authenticated');

create policy "achievements_insert_own" on public.achievements
    for insert with check (auth.uid() = user_id);